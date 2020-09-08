package org.elastos.hive.database;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Result {
	private Map<String, Object> result;

	@SuppressWarnings("unchecked")
	public void deserialize(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        result = mapper.readValue(json, Map.class);
	}

	protected Object get(String name) {
		return result.get(name);
	}
}
