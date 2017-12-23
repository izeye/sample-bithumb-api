package com.izeye.sample.bithumb.service;

import java.util.Map;

import com.izeye.sample.bithumb.Currency;
import com.izeye.sample.bithumb.domain.Orderbook;

/**
 * Bithumb API service.
 *
 * @author Johnny Lim
 */
public interface BithumbApiService {

	Map<String, Object> getTicker(Currency currency);

	Orderbook getOrderbook(Currency currency);

	Map<String, Object> getRecentTransactions(Currency currency);

	Map<String, Object> getAccount(Currency currency);

}
