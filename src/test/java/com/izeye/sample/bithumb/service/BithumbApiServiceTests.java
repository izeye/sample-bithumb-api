package com.izeye.sample.bithumb.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.izeye.sample.bithumb.Currency;
import com.izeye.sample.bithumb.domain.Orderbook;

/**
 * Tests for {@link BithumbApiService}.
 *
 * @author Johnny Lim
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("production")
public class BithumbApiServiceTests {

	@Autowired
	private BithumbApiService bithumbApiService;

	@Test
	public void testGetTicker() {
		Map<String, Object> ticker = this.bithumbApiService.getTicker(Currency.BCH);
		System.out.println(ticker);
	}

	@Test
	public void testGetOrderbook() {
		Orderbook orderbook = this.bithumbApiService.getOrderbook(Currency.BCH);
		System.out.println(orderbook);
	}

	@Test
	public void testGetRecentTransactions() {
		Map<String, Object> recentTransactions = this.bithumbApiService.getRecentTransactions(Currency.BCH);
		System.out.println(recentTransactions);
	}

	// FIXME: Due to the Bithumb service failures, the keys are not available yet.
	@Ignore
	@Test
	public void testGetAccount() {
		Map<String, Object> account = this.bithumbApiService.getAccount(Currency.BCH);
		System.out.println(account);
	}

}
