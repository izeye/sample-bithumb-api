package com.izeye.sample.bithumb.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import lombok.extern.slf4j.Slf4j;

import com.izeye.sample.bithumb.Currency;
import com.izeye.sample.bithumb.domain.Orderbook;
import com.izeye.sample.bithumb.domain.TradingScenario;
import com.izeye.sample.bithumb.domain.TradingScenarioExecution;
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

	private final BithumbApiService bithumbApiService;
	private final TradingService tradingService;

	private volatile boolean running;

	public DefaultAutoTradingService(
			BithumbApiService bithumbApiService, TradingService tradingService) {
		this.bithumbApiService = bithumbApiService;
		this.tradingService = tradingService;
	}

	@Override
	public void start(TradingScenario... scenarios) {
		this.running = true;

		TradingScenarioExecution[] executions = createTradingScenarioExecutions(scenarios);
		while (this.running) {
			for (TradingScenarioExecution execution : executions) {
				runExecution(execution);
			}

			ThreadUtils.delay();
		}
	}

	private TradingScenarioExecution[] createTradingScenarioExecutions(TradingScenario[] scenarios) {
		TradingScenarioExecution[] executions = new TradingScenarioExecution[scenarios.length];
		for (int i = 0; i < executions.length; i++) {
			TradingScenario scenario = scenarios[i];
			executions[i] = new TradingScenarioExecution(scenario);
			executions[i].setBasePrice(getCurrentBasePrice(scenario.getCurrency()));
			executions[i].logPrices();
		}
		return executions;
	}

	void runExecution(TradingScenarioExecution execution) {
		try {
			TradingScenario scenario = execution.getScenario();
			Currency currency = scenario.getCurrency();
			Orderbook orderbook = this.bithumbApiService.getOrderbook(currency);

			int highestBuyPrice = getHighestBuyPrice(orderbook);
			int lowestSellPrice = getLowestSellPrice(orderbook);

			int basePrice = execution.getBasePrice();
			int buyPriceGapInPercentages = calculateGapInPercentages(basePrice, lowestSellPrice);
			int sellPriceGapInPercentages = calculateGapInPercentages(basePrice, highestBuyPrice);

			TradingStrategy strategy = execution.getStrategy();
			if (buyPriceGapInPercentages <= strategy.getBuySignalGapInPercentages()) {
				log.info("basePrice: {}", basePrice);
				log.info("buyPriceGapInPercentages: {}", buyPriceGapInPercentages);
				log.info("Try to buy now: {}", lowestSellPrice);

				this.tradingService.buy(currency, lowestSellPrice, scenario.getCurrencyUnit());

				// FIXME: This should be replaced with the actual buy price.
				int buyPrice = lowestSellPrice;
				execution.buy(buyPrice);
				execution.logPrices();
				execution.logTotalStatistics();
			}
			else if (sellPriceGapInPercentages >= strategy.getSellSignalGapInPercentages()) {
				log.info("basePrice: {}", basePrice);
				log.info("sellPriceGapInPercentages: {}", sellPriceGapInPercentages);
				log.info("Try to sell now: {}", highestBuyPrice);

				this.tradingService.sell(currency, highestBuyPrice, scenario.getCurrencyUnit());

				// FIXME: This should be replaced with the actual sell price.
				int sellPrice = highestBuyPrice;
				execution.sell(sellPrice);
				execution.logPrices();
				execution.logTotalStatistics();
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

	private int calculateGapInPercentages(int baseValue, int value) {
		return (value - baseValue) * 100 / baseValue;
	}

}
