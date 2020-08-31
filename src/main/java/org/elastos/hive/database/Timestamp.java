package org.elastos.hive.database;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class Timestamp extends CustomSerializedObject {
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


	@Override
	public void serialize(JsonGenerator gen, SerializerProvider provider)
			throws IOException {
		gen.writeStartObject();
		gen.writeFieldName("$timestamp");
		gen.writeStartObject();
		gen.writeNumberField("t", t);
		gen.writeNumberField("i", i);
		gen.writeEndObject();
		gen.writeEndObject();
	}
}
