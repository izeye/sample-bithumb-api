package com.izeye.sample.bithumb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import lombok.extern.slf4j.Slf4j;

import com.izeye.sample.bithumb.Currency;
import com.izeye.sample.bithumb.domain.Orderbook;
import com.izeye.sample.bithumb.domain.TradingScenario;
import com.izeye.sample.bithumb.domain.TradingStrategy;
import com.izeye.sample.bithumb.util.ThreadUtils;

/**
 * Default {@link AutoTradingService}.
 *
 * @author Johnny Lim
 */
@Service
@Slf4j
public class DefaultAutoTradingService implements AutoTradingService {

	private static final double BITHUMB_TRADING_FEE_IN_PERCENTAGES = 0.15d;

	@Autowired
	private BithumbApiService bithumbApiService;

	@Autowired
	private TradingService tradingService;

	private volatile boolean running;

	@Override
	public void start(TradingScenario scenario) {
		Currency currency = scenario.getCurrency();
		double currencyUnit = scenario.getCurrencyUnit();

		TradingStrategy strategy = new TradingStrategy(scenario.getSignalGapInPercentages());

		int tradingCount = 0;

		this.running = true;

		int basePrice = getCurrentBasePrice(currency);
		logPrices(basePrice, strategy);

		int totalBuys = 0;
		int totalSells = 0;
		long totalBuyPrice = 0;
		long totalSellPrice = 0;
		while (this.running) {
			try {
				Orderbook orderbook = this.bithumbApiService.getOrderbook(currency);

				int highestBuyPrice = getHighestBuyPrice(orderbook);
				int lowestSellPrice = getLowestSellPrice(orderbook);

				int buyPriceGapInPercentages = calculateGapInPercentages(basePrice, highestBuyPrice);
				int sellPriceGapInPercentages = calculateGapInPercentages(basePrice, lowestSellPrice);

				if (buyPriceGapInPercentages <= strategy.getBuySignalGapInPercentages()) {
					log.info("Try to buy now: {}", lowestSellPrice);

					this.tradingService.buy(currency, lowestSellPrice, currencyUnit);
					totalBuys++;

					// FIXME: This should be replaced with the actual buy price.
					int buyPrice = lowestSellPrice;
					int tradingFee = getTradingFee(buyPrice);

					log.info("Bought now: {} (Fee: {})", buyPrice, tradingFee);

					totalBuyPrice += (buyPrice + tradingFee);
					log.info("Total gain: {}", totalSellPrice - totalBuyPrice);
					log.info("Total buys: {}", totalBuys);
					log.info("Total sells: {}", totalSells);

					basePrice = buyPrice;
					logPrices(basePrice, strategy);
					continue;
				}

				if (sellPriceGapInPercentages >= strategy.getSellSignalGapInPercentages()) {
					log.info("Try to sell now: {}", highestBuyPrice);

					this.tradingService.sell(currency, highestBuyPrice, currencyUnit);
					totalSells++;

					// FIXME: This should be replaced with the actual sell price.
					int sellPrice = highestBuyPrice;
					int tradingFee = getTradingFee(sellPrice);
					log.info("Sold now: {} (Fee: {})", sellPrice, tradingFee);

					totalSellPrice += (sellPrice - tradingFee);
					log.info("Total gain: {}", totalSellPrice - totalBuyPrice);
					log.info("Total buys: {}", totalBuys);
					log.info("Total sells: {}", totalSells);

					basePrice = sellPrice;
					logPrices(basePrice, strategy);
					continue;
				}
			}
			catch (RestClientException ex) {
				log.error("Target server fault?", ex);
			}
			catch (TradingFailedException ex) {
				log.error("Trading failed?", ex);
			}

			ThreadUtils.delay();
		}
	}

	@Override
	public void stop() {
		this.running = false;
	}

	private int getHighestBuyPrice(Orderbook orderbook) {
		return orderbook.getData().getBids().get(0).getPrice();
	}

	private int getLowestSellPrice(Orderbook orderbook) {
		return orderbook.getData().getAsks().get(0).getPrice();
	}

	private int getCurrentBasePrice(Currency currency) {
		Orderbook orderbook = this.bithumbApiService.getOrderbook(currency);
		return (getHighestBuyPrice(orderbook) + getLowestSellPrice(orderbook)) / 2;
	}

	private int getNextBuyPrice(int basePrice, int buySignalGapInPercentages) {
		return applyPercentages(basePrice, buySignalGapInPercentages);
	}

	private int getNextSellPrice(int basePrice, int sellSignalGapInPercentages) {
		return applyPercentages(basePrice, sellSignalGapInPercentages);
	}

	private int applyPercentages(int basePrice, int percentages) {
		return basePrice * (100 + percentages) / 100;
	}

	private int calculateGapInPercentages(int baseValue, int value) {
		return (value - baseValue) * 100 / baseValue;
	}

	private void logPrices(int basePrice, TradingStrategy strategy) {
		log.info("Base price: {}", basePrice);
		log.info("Next buy price: {}", getNextBuyPrice(
				basePrice, strategy.getBuySignalGapInPercentages()));
		log.info("Next sell price: {}", getNextSellPrice(
				basePrice, strategy.getSellSignalGapInPercentages()));
	}

	private int getTradingFee(int price) {
		return (int) (price * BITHUMB_TRADING_FEE_IN_PERCENTAGES / 100);
	}

}
