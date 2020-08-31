package org.elastos.hive.database;

import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Options<T extends Options<T>> extends HashMap<String, Object> {
	private static final long serialVersionUID = -735828709324637994L;

	@SuppressWarnings("unchecked")
	protected T setStringOption(String name, String value) {
		put(name, value);
		return (T)this;
	}

	@SuppressWarnings("unchecked")
	protected T setNumberOption(String name, int value) {
		put(name, value);
		return (T)this;
	}

	@SuppressWarnings("unchecked")
	protected T setNumberOption(String name, long value) {
		put(name, value);
		return (T)this;
	}

	@SuppressWarnings("unchecked")
	protected T setBooleanOption(String name, boolean value) {
		put(name, value);
		return (T)this;
	}

	@SuppressWarnings("unchecked")
	protected T setArrayOption(String name, Object[] value) {
		put(name, value);
		return (T)this;
	}

	@SuppressWarnings("unchecked")
	protected T setObjectOption(String name, JsonNode value) {
		put(name, value);
		return (T)this;
	}

	@SuppressWarnings("unchecked")
	protected T setObjectOption(String name, Object value) {
		put(name, value);
		return (T)this;
	}

	public String serialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.writer().writeValueAsString(this);
	}
}
