package com.izeye.sample.bithumb.domain;

import java.util.List;

/**
 * Order book.
 *
 * @author Johnny Lim
 */
@lombok.Data
public class Orderbook {

	private Data data;

	/**
	 * Data.
	 */
	@lombok.Data
	public static class Data {

		private List<Order> bids;
		private List<Order> asks;

		/**
		 * Order.
		 */
		@lombok.Data
		public static class Order {

			private int price;

		}

	}

}
