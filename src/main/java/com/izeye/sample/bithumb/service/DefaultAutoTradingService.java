package com.izeye.sample.bithumb.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.izeye.sample.bithumb.Currency;
import com.izeye.sample.bithumb.domain.Orderbook;

/**
 * Default {@link AutoTradingService}.
 *
 * @author Johnny Lim
 */
@Service
@Slf4j
public class DefaultAutoTradingService implements AutoTradingService {

	private static final long DEFAULT_DELAY_IN_MILLIS = TimeUnit.SECONDS.toMillis(1);

//	private static final int DEFAULT_SIGNAL_GAP_IN_PERCENTAGES = 3;
//	private static final int DEFAULT_SIGNAL_GAP_IN_PERCENTAGES = 2;
	private static final int DEFAULT_SIGNAL_GAP_IN_PERCENTAGES = 1;
	private static final int DEFAULT_BUY_SIGNAL_GAP_IN_PERCENTAGES = -DEFAULT_SIGNAL_GAP_IN_PERCENTAGES;
	private static final int DEFAULT_SELL_SIGNAL_GAP_IN_PERCENTAGES = DEFAULT_SIGNAL_GAP_IN_PERCENTAGES;

	@Autowired
	private BithumbApiService bithumbApiService;

	@Autowired
	private TradingService tradingService;

	private volatile boolean running;

	@Override
	public void start(Currency currency, double unit) {
		this.running = true;

		int basePrice = getCurrentBasePrice(currency);
		logPrices(basePrice);

		while (this.running) {
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
				log.info("Bought now: {}", buyPrice);

				basePrice = buyPrice;
				logPrices(basePrice);
				continue;
			}

			if (sellPriceGapInPercentages >= DEFAULT_SELL_SIGNAL_GAP_IN_PERCENTAGES) {
				log.info("Try to sell now: {}", highestBuyPrice);

				this.tradingService.sell(currency, highestBuyPrice, unit);

				// FIXME: This should be replaced with the actual sell price.
				int sellPrice = highestBuyPrice;
				log.info("Sold now: {}", sellPrice);

				basePrice = sellPrice;
				logPrices(basePrice);
				continue;
			}

			delay();
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

	private void delay() {
		try {
			Thread.sleep(DEFAULT_DELAY_IN_MILLIS);
		}
		catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
	}

}
