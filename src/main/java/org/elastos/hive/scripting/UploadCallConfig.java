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

	UploadCallConfig(String appDid, JsonNode params, String filePath) {
		super(appDid, params);
		this.filePath = filePath;
	}

	public String filePath() {
		return this.filePath;
	}
}
