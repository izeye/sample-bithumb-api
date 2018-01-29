package com.izeye.sample.bithumb.service;

import com.izeye.sample.bithumb.domain.Currency;

/**
 * Trading service.
 *
 * @author Johnny Lim
 */
public interface TradingService {

	void buy(Currency currency, int price, double amount);

	void sell(Currency currency, int price, double amount);

}
