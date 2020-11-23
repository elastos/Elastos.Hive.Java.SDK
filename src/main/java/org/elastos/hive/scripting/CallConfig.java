package org.elastos.hive.scripting;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * scripting call config class
 */
public class CallConfig<T> {

	/**
	 * Construction method
	 * @param appDid
	 * @param params
	 */
	public CallConfig(String appDid, JsonNode params) {

	}

	/**
	 * Call parameters (params field) are meant to be used by scripts on the server side, for example as injected parameters
	 * to mongo queries. Ex: if "params" contains a field "name":"someone", then the called script is able to reference this parameter
	 * using "$params.name".
	 */
	private JsonNode params;
	/**
	 * used for cross did, optional parameter
	 */
	private String appDid;

	public String appDid() {
		return this.appDid;
	}

	public JsonNode params() {
		return this.params;
	}
}