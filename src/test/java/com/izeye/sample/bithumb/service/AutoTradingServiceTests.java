package com.izeye.sample.bithumb.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.izeye.sample.bithumb.Currency;
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

	private static final int LOGIN_WAIT_SECONDS = 60;

	private static final TradingScenario SCENARIO_1 = new TradingScenario(Currency.XRP, 1, 1);
	private static final TradingScenario SCENARIO_2 = new TradingScenario(Currency.XRP, 2, 2);
	private static final TradingScenario SCENARIO_3 = new TradingScenario(Currency.XRP, 3, 3);

	@Autowired
	private AutoTradingService autoTradingService;

//	// NOTE: Manual login involved to pass this test.
//	@Ignore
	@Test
	public void runScenarios() {
//		startAutoTradingServiceStopThread();

		waitLogin();

		this.autoTradingService.start(SCENARIO_1);
//		this.autoTradingService.start(SCENARIO_1, SCENARIO_2, SCENARIO_3);
	}

	private void startAutoTradingServiceStopThread() {
		new Thread(() -> {
			try {
				Thread.sleep(TimeUnit.MINUTES.toMillis(1));
				this.autoTradingService.stop();
			}
			catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			}
		}).start();
	}

	private void waitLogin() {
		try {
			System.out.println("Sleeping " + LOGIN_WAIT_SECONDS + " second(s)...");
			for (int i = 0; i < LOGIN_WAIT_SECONDS; i++) {
				Thread.sleep(TimeUnit.SECONDS.toMillis(1));
				System.out.println("Elapsed " + (i + 1) + " second(s)");
			}
		}
		catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
	}

}
