package com.izeye.sample.bithumb.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.izeye.sample.bithumb.Currency;

/**
 * Tests for {@link BithumbApiService}.
 *
 * @author Johnny Lim
 */
@RunWith(SpringRunner.class)
@SpringBootTest
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
		Map<String, Object> orderbook = this.bithumbApiService.getOrderbook(Currency.BCH);
		System.out.println(orderbook);
	}

	@Test
	public void testGetRecentTransactions() {
		Map<String, Object> recentTransactions = this.bithumbApiService.getRecentTransactions(Currency.BCH);
		System.out.println(recentTransactions);
	}

}
