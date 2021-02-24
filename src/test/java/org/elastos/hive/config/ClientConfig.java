package org.elastos.hive.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientConfig extends Config {
	@JsonProperty("applicationConfig")
	private ApplicationConfig applicationConfig;
	@JsonProperty("userConfig")
	private UserConfig userConfig;
	@JsonProperty("nodeConfig")
	private NodeConfig nodeConfig;

	public ApplicationConfig applicationConfig() {
		return this.applicationConfig;
	}

	public UserConfig userConfig() {
		return this.userConfig;
	}

	public NodeConfig nodeConfig() {
		return this.nodeConfig;
	}

	public static ClientConfig deserialize(String content) {
		return deserialize(content, ClientConfig.class);
	}
}
