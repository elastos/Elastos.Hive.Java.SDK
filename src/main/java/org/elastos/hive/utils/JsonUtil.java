package org.elastos.hive.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
	public static String serialize(Object object) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static JsonNode deserialize(String json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readTree(json);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
