package org.elastos.hive.scripting;

import com.fasterxml.jackson.databind.JsonNode;

public class UploadCallConfig<T> extends CallConfig {
	private String filePath;



	public UploadCallConfig(JsonNode params, String filePath, Class<T> resultType) {
		this(null, params, resultType);
		this.filePath = filePath;
	}

	public UploadCallConfig(String appDid, JsonNode params, String filePath, Class<T> resultType) {
		this(appDid, params, resultType);
		this.filePath = filePath;
	}

	UploadCallConfig(String appDid, JsonNode params, Class<T> resultType) {
		super(appDid, params, resultType);
	}
}
