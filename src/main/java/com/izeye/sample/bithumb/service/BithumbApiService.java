package com.izeye.sample.bithumb.service;

import java.util.Map;

import com.izeye.sample.bithumb.domain.CryptoCurrency;
import com.izeye.sample.bithumb.domain.Orderbook;

/**
 * Bithumb API service.
 *
 * @author Johnny Lim
 */
public interface BithumbApiService {

	Map<String, Object> getTicker(CryptoCurrency currency);

	Orderbook getOrderbook(CryptoCurrency currency);

	Map<String, Object> getRecentTransactions(CryptoCurrency currency);

	Map<String, Object> getAccount(CryptoCurrency currency);

}
