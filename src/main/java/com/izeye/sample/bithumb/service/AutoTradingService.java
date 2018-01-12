package com.izeye.sample.bithumb.service;

import com.izeye.sample.bithumb.domain.TradingScenario;

/**
 * Automated trading service.
 *
 * @author Johnny Lim
 */
public interface AutoTradingService {

	void start(TradingScenario scenario);

	void stop();

}
