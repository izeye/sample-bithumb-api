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

	@Autowired
	private AutoTradingService autoTradingService;

//	// NOTE: Manual login involved to pass this test.
//	@Ignore
	@Test
	public void test() {
//		startAutoTradingServiceStopThread();

		waitLogin();

		this.autoTradingService.start(Currency.XRP, 2);
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
