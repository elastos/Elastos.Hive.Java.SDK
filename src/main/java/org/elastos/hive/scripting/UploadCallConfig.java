package org.elastos.hive.scripting;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Scripting file upload call config
 */
public class UploadCallConfig extends CallConfig {
	private String filePath;

	public UploadCallConfig(JsonNode params, String filePath) {
		this(null, params, filePath);
	}

	public UploadCallConfig(String appDid, JsonNode params, String filePath) {
		this(appDid, params);
		this.filePath = filePath;
	}

	UploadCallConfig(String appDid, JsonNode params) {
		super(appDid, params);
	}

	public String filePath() {
		return this.filePath;
	}
}
