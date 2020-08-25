package org.elastos.hive.database;

public class Index {
	private String key;
	private Order order;

	public enum Order {
		ASCENDING(1),
		DESCENDING(-1);

		private int value;

		Order(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}
	}

	public Index(String key, Order order) {
		this.key = key;
		this.order = order;
	}

	public String getKey() {
		return key;
	}

	public Order getOrder() {
		return order;
	}
}
