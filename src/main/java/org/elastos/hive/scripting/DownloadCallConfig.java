package org.elastos.hive.scripting;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Scripting file download call config
 */
public class DownloadCallConfig extends CallConfig {

	/**
	 * Construction method
	 *
	 * @param params
	 */
	public DownloadCallConfig(JsonNode params) {
		this(null, params);
	}

	/**
	 * Construction method
	 *
	 * @param appDid
	 * @param params
	 */
	public DownloadCallConfig(String appDid, JsonNode params) {
		super(appDid, params);
	}
}
