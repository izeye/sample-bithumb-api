package com.izeye.sample.bithumb.service;

import java.util.concurrent.TimeUnit;

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
		long startTime = System.currentTimeMillis();

		Currency currency = scenario.getCurrency();
		double currencyUnit = scenario.getCurrencyUnit();

		TradingStrategy strategy = new TradingStrategy(scenario.getSignalGapInPercentages());

		this.running = true;

		int basePrice = getCurrentBasePrice(currency);
		logPrices(basePrice, strategy);

		int totalBuyUnits = 0;
		int totalSellUnits = 0;
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
					totalBuyUnits += currencyUnit;

					// FIXME: This should be replaced with the actual buy price.
					int buyPrice = lowestSellPrice;
					int subTotalBuyPrice = (int) (buyPrice * currencyUnit);
					int tradingFee = getTradingFee(subTotalBuyPrice);
					log.info("Bought now: {} (Fee: {})", buyPrice, tradingFee);

					basePrice = buyPrice;
					logPrices(basePrice, strategy);

					totalBuyPrice += (subTotalBuyPrice + tradingFee);
					logTotalStatistics(
							totalBuyUnits, totalSellUnits, totalBuyPrice, totalSellPrice, basePrice, startTime);
					continue;
				}

				if (sellPriceGapInPercentages >= strategy.getSellSignalGapInPercentages()) {
					log.info("Try to sell now: {}", highestBuyPrice);

					this.tradingService.sell(currency, highestBuyPrice, currencyUnit);
					totalSellUnits += currencyUnit;

					// FIXME: This should be replaced with the actual sell price.
					int sellPrice = highestBuyPrice;
					int subTotalSellPrice = (int) (sellPrice * currencyUnit);
					int tradingFee = getTradingFee(subTotalSellPrice);
					log.info("Sold now: {} (Fee: {})", sellPrice, tradingFee);

					basePrice = sellPrice;
					logPrices(basePrice, strategy);

					totalSellPrice += (subTotalSellPrice - tradingFee);
					logTotalStatistics(
							totalBuyUnits, totalSellUnits, totalBuyPrice, totalSellPrice, basePrice, startTime);
					continue;
				}
			}
			catch (RestClientException ex) {
				log.warn("Target server fault: {}", ex.getMessage());
				log.debug("Target server fault.", ex);
			}
			catch (TradingFailedException ex) {
				log.warn("Trading failed: {}", ex.getMessage());
				log.debug("Trading failed.", ex);
			}

			ThreadUtils.delay();
		}
	}

	private void logTotalStatistics(
			int totalBuyUnits, int totalSellUnits, long totalBuyPrice, long totalSellPrice,
			int currentPrice, long startTime) {
		long elapsedTimeInMillis = System.currentTimeMillis() - startTime;
		log.info("Elapsed time: {} minute(s)", TimeUnit.MILLISECONDS.toMinutes(elapsedTimeInMillis));
		log.info("Total gain: {}", totalSellPrice - totalBuyPrice);
		int estimatedAdditionalGain = (totalBuyUnits - totalSellUnits) * currentPrice;
		log.info("Estimated total gain: {}", totalSellPrice - totalBuyPrice + estimatedAdditionalGain);
		log.info("Total buy units: {}", totalBuyUnits);
		log.info("Total sell units: {}", totalSellUnits);
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
