package org.elastos.hive.demo.sdk.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Main class for configure file.
 */
public class ClientConfig extends BaseConfig {
	@JsonProperty("resolverUrl")
	private String resolverUrl;
	@JsonProperty("application")
	private ApplicationConfig applicationConfig;
	@JsonProperty("user")
	private UserConfig userConfig;
	@JsonProperty("node")
	private NodeConfig nodeConfig;
	@JsonProperty("cross")
	private CrossConfig crossConfig;

	public String resolverUrl() {
		return this.resolverUrl;
	}

	public ApplicationConfig applicationConfig() {
		return this.applicationConfig;
	}

	public UserConfig userConfig() {
		return this.userConfig;
	}

	public NodeConfig nodeConfig() {
		return this.nodeConfig;
	}

	public CrossConfig crossConfig() {
		return this.crossConfig;
	}

	public static ClientConfig deserialize(String content) {
		return deserialize(content, ClientConfig.class);
	}
}
