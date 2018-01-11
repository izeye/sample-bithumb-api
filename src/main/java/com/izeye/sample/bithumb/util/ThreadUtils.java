package com.izeye.sample.bithumb.util;

import java.util.concurrent.TimeUnit;

/**
 * Utilities for {@link Thread}.
 *
 * @author Johnny Lim
 */
public final class ThreadUtils {

	private static final long DEFAULT_DELAY_IN_MILLIS = TimeUnit.SECONDS.toMillis(1);

	public static void delay() {
		try {
			Thread.sleep(DEFAULT_DELAY_IN_MILLIS);
		}
		catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
	}

	private ThreadUtils() {
	}

}
