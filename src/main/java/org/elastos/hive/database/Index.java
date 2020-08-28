package org.elastos.hive.database;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class Index extends CustomSerializedObject {
	private String key;
	private Order order;

	public enum Order {
		ASCENDING(1), DESCENDING(-1);

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

	@Override
	public void serialize(JsonGenerator gen, SerializerProvider provider)
			throws IOException {
		gen.writeStartObject();
		gen.writeNumberField(getKey(), getOrder().value());
		gen.writeEndObject();
	}
}
