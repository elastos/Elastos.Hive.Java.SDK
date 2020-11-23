package org.elastos.hive.scripting;

import com.fasterxml.jackson.databind.JsonNode;

public class DownloadCallConfig<T> extends CallConfig {

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
