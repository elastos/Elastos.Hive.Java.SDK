package org.elastos.hive.database;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public abstract class CustomSerializedObject implements JsonSerializable {
	@Override
	public abstract void serialize(JsonGenerator gen, SerializerProvider provider)
			throws IOException;

	@Override
	public void serializeWithType(JsonGenerator gen,
			SerializerProvider provider, TypeSerializer typeSer)
			throws IOException {
		WritableTypeId id = typeSer.typeId(this, this.getClass(), JsonToken.START_OBJECT);
	    typeSer.writeTypePrefix(gen, id);

	    serialize(gen, provider);

		id = typeSer.typeId(this, this.getClass(), JsonToken.END_OBJECT);
	    typeSer.writeTypeSuffix(gen, id);
	}

}
