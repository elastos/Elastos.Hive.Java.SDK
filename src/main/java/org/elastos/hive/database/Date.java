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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@JsonSerialize(using = Date.Serializer.class)
@JsonDeserialize(using = Date.Deserializer.class)
public class Date {
	private java.util.Date date;
	private static final String DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	private static final SimpleDateFormat isoDateFormat =
			new SimpleDateFormat(DATE_FORMAT_ISO_8601);

	public Date() {
		this(Calendar.getInstance());
	}

	public Date(java.util.Date date) {
		this.date = date;
	}

	public Date(Calendar cal) {
		this.date = cal.getTime();
	}

	public java.util.Date getDate() {
		return date;
	}

	static class Serializer extends StdSerializer<Date> {
		private static final long serialVersionUID = -6104869695567019883L;

		public Serializer() {
	        this(null);
	    }

	    public Serializer(Class<Date> t) {
	        super(t);
	    }

		@Override
		public void serialize(Date date, JsonGenerator gen,
							  SerializerProvider provider) throws IOException {
			gen.writeStartObject();
			gen.writeFieldName("$date");
			if (date.date == null)
				gen.writeNull();
			else
				gen.writeString(isoDateFormat.format(date.date));
			gen.writeEndObject();
		}
	}

	static class Deserializer extends StdDeserializer<Date> {
		private static final long serialVersionUID = 2826549765201434279L;

		public Deserializer() {
	        this(null);
	    }

	    public Deserializer(Class<?> t) {
	        super(t);
	    }

		@Override
		public Date deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			JsonNode node = p.getCodec().readTree(p);
			if (node.size() != 1)
				throw ctxt.weirdNativeValueException(node, Date.class);

			JsonNode value = node.get("$date");
			if (value == null) {
				throw ctxt.weirdNativeValueException(node, Date.class);
			} else if (value.getNodeType() == JsonNodeType.NULL) {
				return new Date((java.util.Date)null);
			} else if (value.getNodeType() == JsonNodeType.STRING) {
				try {
					return new Date(isoDateFormat.parse(value.asText()));
				} catch (ParseException e) {
					ctxt.weirdStringException(value.asText(), java.util.Date.class, e.getMessage());
				}
			}

			throw ctxt.weirdNativeValueException(node, Date.class);
		}
	}
}
