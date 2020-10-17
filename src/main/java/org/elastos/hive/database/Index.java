package org.elastos.hive.database;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@JsonSerialize(using = Index.Serializer.class)
@JsonDeserialize(using = Index.Deserializer.class)
public class Index {
	private String key;
	private Order order;

	public enum Order {
		ASCENDING(1), DESCENDING(-1);

		private int value;

		Order(int value) {
			this.value = value;
		}

		@JsonValue
		public int value() {
			return value;
		}

		@JsonCreator
		public static Order fromInt(int i) {
		    switch (i) {
		    case 1:
		    	return ASCENDING;

		    case -1:
		    	return DESCENDING;

		    default:
		    	throw new IllegalArgumentException("Invalid index order");
		    }
		}

		@JsonCreator
		public static Order fromString(String name) {
			return valueOf(name.toUpperCase());
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

	static class Serializer extends StdSerializer<Index> {
		private static final long serialVersionUID = 4814086994004868202L;

		public Serializer() {
	        this(null);
	    }

	    public Serializer(Class<Index> t) {
	        super(t);
	    }

		@Override
		public void serialize(Index index, JsonGenerator gen,
				SerializerProvider provider) throws IOException {
			gen.writeStartObject();
			gen.writeNumberField(index.getKey(), index.getOrder().value());
			gen.writeEndObject();
		}
	}

	static class Deserializer extends StdDeserializer<Index> {
		private static final long serialVersionUID = -6327506026962351443L;

		public Deserializer() {
	        this(null);
	    }

	    public Deserializer(Class<?> t) {
	        super(t);
	    }

		@Override
		public Index deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			JsonNode node = p.getCodec().readTree(p);
			if (node.size() != 1)
				throw ctxt.weirdNativeValueException(node, Index.class);

			Iterator<Map.Entry<String,JsonNode>> it = node.fields();
			Map.Entry<String,JsonNode> field = it.next();
			String key = field.getKey();
			Order order = Order.fromInt(field.getValue().asInt());
			return new Index(key, order);
		}
	}
}
