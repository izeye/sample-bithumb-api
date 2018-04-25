package com.izeye.sample.bithumb.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.izeye.sample.bithumb.domain.CryptoCurrency;

/**
 * Tests for {@link SeleniumBithumbTradingService}.
 *
 * @author Johnny Lim
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SeleniumBithumbTradingServiceTests {

	@Autowired
	private SeleniumBithumbTradingService tradingService;

	@Ignore
	@Test
	public void buy() {
		waitForAuthentication();

		this.tradingService.buy(CryptoCurrency.XRP, 950, 1);
	}

	private void waitForAuthentication() {
		try {
			for (int i = 1; i < 60; i++) {
				Thread.sleep(TimeUnit.SECONDS.toMillis(1));
				System.out.println("Elapsed " + i + " second(s).");
			}
		}
		catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(ex);
		}
	}

}
