package org.elastos.hive.database;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class Index {
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

	public static class Serializer extends StdSerializer<Index> {
		private static final long serialVersionUID = 2740013994422988258L;

		protected Serializer() {
			this(null);
		}

		protected Serializer(Class<Index> t) {
			super(t);
		}

		@Override
		public void serialize(Index value, JsonGenerator gen,
				SerializerProvider provider) throws IOException {
			gen.writeStartObject();
			gen.writeNumberField(value.getKey(), value.getOrder().value());
			gen.writeEndObject();
		}
	}
}
