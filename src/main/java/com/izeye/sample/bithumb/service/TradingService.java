package com.izeye.sample.bithumb.service;

import com.izeye.sample.bithumb.domain.CryptoCurrency;

/**
 * Trading service.
 *
 * @author Johnny Lim
 */
public interface TradingService {

	void buy(CryptoCurrency currency, int price, double amount);

	void sell(CryptoCurrency currency, int price, double amount);

}
