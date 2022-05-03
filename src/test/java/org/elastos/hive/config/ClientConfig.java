package org.elastos.hive.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientConfig extends Config {
	@JsonProperty("resolverUrl")
	private String resolverUrl;
	@JsonProperty("ipfsGateUrl")
	private String ipfsGateUrl;
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

	public String getIpfsGateUrl() { return this.ipfsGateUrl; }

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
