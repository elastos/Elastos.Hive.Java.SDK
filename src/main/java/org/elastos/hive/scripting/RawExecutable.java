package org.elastos.hive.scripting;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class RawExecutable extends Executable implements JsonSerializable {
	private static final String TYPE = "raw";
	private String executable;

	public RawExecutable(String executable) {
		super(TYPE, null);
		this.executable = executable;
	}

	@Override
	public Object getBody() {
		return null;
	}

	@Override
	public void serialize(JsonGenerator gen, SerializerProvider provider)
			throws IOException {
		gen.writeRawValue(executable);
	}

	@Override
	public void serializeWithType(JsonGenerator gen,
			SerializerProvider provider, TypeSerializer typeSer)
			throws IOException {
		serialize(gen, provider);
	}
}
