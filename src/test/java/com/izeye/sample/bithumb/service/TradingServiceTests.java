package com.izeye.sample.bithumb.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.izeye.sample.bithumb.domain.Currency;

/**
 * Tests for {@link TradingService}.
 *
 * @author Johnny Lim
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TradingServiceTests {

	private static final int LOGIN_WAIT_SECONDS = 30;

	@Autowired
	private TradingService tradingService;

	// NOTE: Manual login involved to pass this test.
	@Ignore
	@Test
	public void testBuy() {
		waitLogin();

		this.tradingService.buy(Currency.XRP, 4100, 1);
	}

	// NOTE: Manual login involved to pass this test.
	@Ignore
	@Test
	public void testSell() {
		waitLogin();

		this.tradingService.sell(Currency.XRP, 4150, 1);
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
