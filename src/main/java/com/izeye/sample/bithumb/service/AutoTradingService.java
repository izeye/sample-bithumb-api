package com.izeye.sample.bithumb.service;

import com.izeye.sample.bithumb.Currency;

/**
 * Automated trading service.
 *
 * @author Johnny Lim
 */
public interface AutoTradingService {

	void start(Currency currency, double unit);

	void stop();

}
