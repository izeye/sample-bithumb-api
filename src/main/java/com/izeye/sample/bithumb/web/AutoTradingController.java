package com.izeye.sample.bithumb.web;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.izeye.sample.bithumb.domain.CryptoCurrency;
import com.izeye.sample.bithumb.domain.TradingScenarioFactory;
import com.izeye.sample.bithumb.service.AutoTradingService;

/**
 * {@link RestController} for auto-trading.
 *
 * @author Johnny Lim
 */
@RestController
@RequestMapping(path = "/auto-trading")
public class AutoTradingController {

	@Autowired
	private AutoTradingService autoTradingService;

	private final ExecutorService executorService = Executors.newFixedThreadPool(10);

	@GetMapping("/start")
	public void start() {
		this.executorService.submit(() -> {
			this.autoTradingService.start(TradingScenarioFactory.createLinearScenarios(CryptoCurrency.XRP, 1, 10));
		});
	}

}
