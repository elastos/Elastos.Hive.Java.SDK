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

@JsonSerialize(using = RegularExpression.Serializer.class)
@JsonDeserialize(using = RegularExpression.Deserializer.class)
public class RegularExpression {
	private String pattern;
	private String options;

	public RegularExpression(String pattern, String options) {
		this.pattern = pattern;
		this.options = options;
	}

	public RegularExpression(String pattern) {
		this(pattern, null);
	}

	public String getPattern() {
		return pattern;
	}

	public String getOptions() {
		return options;
	}

	static class Serializer extends StdSerializer<RegularExpression> {
		private static final long serialVersionUID = 7989096423894867789L;

		public Serializer() {
	        this(null);
	    }

	    public Serializer(Class<RegularExpression> t) {
	        super(t);
	    }

		@Override
		public void serialize(RegularExpression regex, JsonGenerator gen,
							  SerializerProvider provider) throws IOException {
			gen.writeStartObject();
			gen.writeFieldName("$regularExpression");
			gen.writeStartObject();
			gen.writeStringField("pattern", regex.getPattern());
			if (regex.getOptions() != null)
				gen.writeStringField("options", regex.getOptions());
			gen.writeEndObject();
			gen.writeEndObject();
		}
	}

	static class Deserializer extends StdDeserializer<RegularExpression> {
		private static final long serialVersionUID = 4754690328051740293L;

		public Deserializer() {
	        this(null);
	    }

	    public Deserializer(Class<?> t) {
	        super(t);
	    }

		@Override
		public RegularExpression deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			JsonNode node = p.getCodec().readTree(p);
			if (node.size() != 1)
				throw ctxt.weirdNativeValueException(node, RegularExpression.class);

			JsonNode value = node.get("$regularExpression");
			if (value != null && value.getNodeType() == JsonNodeType.OBJECT) {
				JsonNode pattern = value.get("pattern");
				if (pattern == null || pattern.getNodeType() != JsonNodeType.STRING)
					throw ctxt.weirdNativeValueException(node, RegularExpression.class);

				JsonNode options = value.get("options");
				if (options != null && options.getNodeType() != JsonNodeType.STRING)
					throw ctxt.weirdNativeValueException(node, RegularExpression.class);

				return new RegularExpression(pattern.asText(), options == null ? null : options.asText());
			}

			throw ctxt.weirdNativeValueException(node, RegularExpression.class);
		}
	}
}
