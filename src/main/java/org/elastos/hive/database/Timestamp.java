package org.elastos.hive.database;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

@JsonSerialize(using = Timestamp.Serializer.class)
@JsonDeserialize(using = Timestamp.Deserializer.class)
public class Timestamp {
	private long t;
	private long i;

	public Timestamp(long t, long i) {
		this.t = t;
		this.i = i;
	}

	public long getT() {
		return t;
	}

	public long getI() {
		return i;
	}

	static class Serializer extends StdSerializer<Timestamp> {
		private static final long serialVersionUID = 3255376008348898188L;

		public Serializer() {
	        this(null);
	    }

	    public Serializer(Class<Timestamp> t) {
	        super(t);
	    }

		@Override
		public void serialize(Timestamp ts, JsonGenerator gen,
							  SerializerProvider provider) throws IOException {
			gen.writeStartObject();
			gen.writeFieldName("$timestamp");
			gen.writeStartObject();
			gen.writeNumberField("t", ts.t);
			gen.writeNumberField("i", ts.i);
			gen.writeEndObject();
			gen.writeEndObject();
		}
	}

	static class Deserializer extends StdDeserializer<Timestamp> {
		private static final long serialVersionUID = 1287867420875833742L;

		public Deserializer() {
	        this(null);
	    }

	    public Deserializer(Class<?> t) {
	        super(t);
	    }

		@Override
		public Timestamp deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			JsonNode node = p.getCodec().readTree(p);
			if (node.size() != 1)
				throw ctxt.weirdNativeValueException(node, IOException.class);

			JsonNode value = node.get("$timestamp");
			if (value != null && value.getNodeType() == JsonNodeType.OBJECT) {
				JsonNode t = value.get("t");
				if (t == null || t.getNodeType() != JsonNodeType.NUMBER)
					throw ctxt.weirdNativeValueException(node, IOException.class);

				JsonNode i = value.get("i");
				if (i == null || i.getNodeType() != JsonNodeType.NUMBER)
					throw ctxt.weirdNativeValueException(node, IOException.class);

				return new Timestamp(t.asLong(), i.asLong());
			}

			throw ctxt.weirdNativeValueException(node, IOException.class);
		}
	}
}
