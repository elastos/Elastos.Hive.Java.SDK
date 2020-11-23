package org.elastos.hive.scripting;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Scripting general call config
 */
public class GeneralCallConfig extends CallConfig{

	/**
	 * Construction method
	 *
	 * @param params
	 * @param resultType String, byte[], JsonNode, Reader
	 */
	public GeneralCallConfig(JsonNode params, Class resultType) {
		this(null, params, resultType);
	}

	/**
	 * Construction method
	 *
	 * @param appDid
	 * @param params
	 * @param resultType String, byte[], JsonNode, Reader
	 */
	public GeneralCallConfig(String appDid, JsonNode params, Class resultType) {
		super(appDid, params, resultType);
	}
}
