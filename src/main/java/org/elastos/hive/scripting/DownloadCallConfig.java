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
	 * @param resultType String, byte[], JsonNode, Reader
	 */
	public DownloadCallConfig(JsonNode params, Class<T> resultType) {
		this(null, params, resultType);
	}

	/**
	 * Construction method
	 *
	 * @param appDid
	 * @param params
	 * @param resultType String, byte[], JsonNode, Reader
	 */
	public DownloadCallConfig(String appDid, JsonNode params, Class<T> resultType) {
		super(appDid, params, resultType);
	}
}
