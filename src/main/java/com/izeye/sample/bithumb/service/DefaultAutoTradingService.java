package com.izeye.sample.bithumb.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import lombok.extern.slf4j.Slf4j;

import com.izeye.sample.bithumb.Currency;
import com.izeye.sample.bithumb.domain.Orderbook;
import com.izeye.sample.bithumb.util.ThreadUtils;

/**
 * Default {@link AutoTradingService}.
 *
 * @author Johnny Lim
 */
@Service
@Slf4j
public class DefaultAutoTradingService implements AutoTradingService {

//	private static final int DEFAULT_SIGNAL_GAP_IN_PERCENTAGES = 3;
	private static final int DEFAULT_SIGNAL_GAP_IN_PERCENTAGES = 2;
//	private static final int DEFAULT_SIGNAL_GAP_IN_PERCENTAGES = 1;
	private static final int DEFAULT_BUY_SIGNAL_GAP_IN_PERCENTAGES = -DEFAULT_SIGNAL_GAP_IN_PERCENTAGES;
	private static final int DEFAULT_SELL_SIGNAL_GAP_IN_PERCENTAGES = DEFAULT_SIGNAL_GAP_IN_PERCENTAGES;

	@Autowired
	private BithumbApiService bithumbApiService;

	@Autowired
	private TradingService tradingService;

	private volatile boolean running;

	@Override
	public void start(Currency currency, double unit) {
		int tradingCount = 0;

		this.running = true;

		int basePrice = getCurrentBasePrice(currency);
		logPrices(basePrice);

		while (this.running) {
			try {
				Orderbook orderbook = this.bithumbApiService.getOrderbook(currency);

				int highestBuyPrice = getHighestBuyPrice(orderbook);
				int lowestSellPrice = getLowestSellPrice(orderbook);

				int buyPriceGapInPercentages = calculateGapInPercentages(basePrice, highestBuyPrice);
				int sellPriceGapInPercentages = calculateGapInPercentages(basePrice, lowestSellPrice);

				if (buyPriceGapInPercentages <= DEFAULT_BUY_SIGNAL_GAP_IN_PERCENTAGES) {
					log.info("Try to buy now: {}", lowestSellPrice);

					this.tradingService.buy(currency, lowestSellPrice, unit);

					// FIXME: This should be replaced with the actual buy price.
					int buyPrice = lowestSellPrice;
					tradingCount++;
					log.info("[{}] Bought now: {}", tradingCount, buyPrice);

					basePrice = buyPrice;
					logPrices(basePrice);
					continue;
				}

				if (sellPriceGapInPercentages >= DEFAULT_SELL_SIGNAL_GAP_IN_PERCENTAGES) {
					log.info("Try to sell now: {}", highestBuyPrice);

					this.tradingService.sell(currency, highestBuyPrice, unit);

					// FIXME: This should be replaced with the actual sell price.
					int sellPrice = highestBuyPrice;
					tradingCount++;
					log.info("[{}] Sold now: {}", tradingCount, sellPrice);

					basePrice = sellPrice;
					logPrices(basePrice);
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

	private int getNextBuyPrice(int basePrice) {
		return applyPercentages(basePrice, DEFAULT_BUY_SIGNAL_GAP_IN_PERCENTAGES);
	}

	private int getNextSellPrice(int basePrice) {
		return applyPercentages(basePrice, DEFAULT_SELL_SIGNAL_GAP_IN_PERCENTAGES);
	}

	private int applyPercentages(int basePrice, int percentages) {
		return basePrice * (100 + percentages) / 100;
	}

	private int calculateGapInPercentages(int baseValue, int value) {
		return (value - baseValue) * 100 / baseValue;
	}

	private void logPrices(int basePrice) {
		log.info("Base price: {}", basePrice);
		log.info("Next buy price: {}", getNextBuyPrice(basePrice));
		log.info("Next sell price: {}", getNextSellPrice(basePrice));
	}

}
