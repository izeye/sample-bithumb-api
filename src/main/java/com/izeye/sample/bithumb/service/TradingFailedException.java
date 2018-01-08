package com.izeye.sample.bithumb.service;

/**
 * {@link RuntimeException} for trading failures.
 *
 * @author Johnny Lim
 */
public class TradingFailedException extends RuntimeException {

	public TradingFailedException(String message) {
		super(message);
	}

	public TradingFailedException(String message, Throwable cause) {
		super(message, cause);
	}

}
