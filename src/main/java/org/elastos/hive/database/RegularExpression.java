package org.elastos.hive.database;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class RegularExpression extends CustomSerializedObject {
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

	@Override
	public void serialize(JsonGenerator gen, SerializerProvider provider)
			throws IOException {
		gen.writeStartObject();
		gen.writeFieldName("$regularExpression");
		gen.writeStartObject();
		gen.writeStringField("pattern", getPattern());
		if (getOptions() != null)
			gen.writeStringField("options", getOptions());
		gen.writeEndObject();
		gen.writeEndObject();
	}
}
