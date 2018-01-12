package com.izeye.sample.bithumb.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import com.izeye.sample.bithumb.Currency;

/**
 * Trading scenario.
 *
 * @author Johnny Lim
 */
@Data
@AllArgsConstructor
public class TradingScenario {

	private Currency currency;
	private double currencyUnit;
	private int signalGapInPercentages;

}
