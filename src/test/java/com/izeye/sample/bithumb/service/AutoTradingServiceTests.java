package com.izeye.sample.bithumb.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.izeye.sample.bithumb.domain.CryptoCurrency;
import com.izeye.sample.bithumb.domain.TradingScenario;

/**
 * Tests for {@link AutoTradingService}.
 *
 * @author Johnny Lim
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("production")
public class AutoTradingServiceTests {

	@Autowired
	private AutoTradingService autoTradingService;

	@Test
	public void runScenarios() {
		startAutoTradingServiceStopThread();

		this.autoTradingService.start(new TradingScenario(CryptoCurrency.XRP, 1, 1));
	}

	private void startAutoTradingServiceStopThread() {
		new Thread(() -> {
			try {
				Thread.sleep(TimeUnit.SECONDS.toMillis(10));
				this.autoTradingService.stop();
			}
			catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			}
		}).start();
	}

}
