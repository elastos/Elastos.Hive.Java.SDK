package org.elastos.hive.scripting;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Scripting general call config
 */
public class GeneralCallConfig<T> extends CallConfig {

	public GeneralCallConfig() {
		this(null, null);
	}

	/**
	 * Construction method
	 *
	 * @param params
	 */
	public GeneralCallConfig(JsonNode params) {
		this(null, params);
	}

	/**
	 * Construction method
	 *
	 * @param appDid
	 * @param params
	 */
	public GeneralCallConfig(String appDid, JsonNode params) {
		super(appDid, params);
	}
}
