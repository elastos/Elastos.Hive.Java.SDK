package org.elastos.hive.scripting;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Scripting file download call config
 * @param <T> InputStream, Reader
 */
public class DownloadCallConfig<T> extends CallConfig {

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
