package com.izeye.sample.bithumb.domain;

import lombok.Data;

/**
 * Trading strategy.
 *
 * @author Johnny Lim
 */
@Data
public class TradingStrategy {

	private final int buySignalGapInPercentages;
	private final int sellSignalGapInPercentages;

	public TradingStrategy(int signalGapInPercentages) {
		this.buySignalGapInPercentages = -signalGapInPercentages;
		this.sellSignalGapInPercentages = signalGapInPercentages;
	}

}
