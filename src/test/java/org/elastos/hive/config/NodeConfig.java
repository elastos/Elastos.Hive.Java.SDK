package org.elastos.hive.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NodeConfig extends Config {
	@JsonProperty("ownerDid")
	private String ownerDid;
	@JsonProperty("provider")
	private String provider;
	@JsonProperty("targetDid")
	private String targetDid;
	@JsonProperty("targetHost")
	private String targetHost;
	@JsonProperty("storePath")
	private String storePath;

	public String ownerDid() {
		return this.ownerDid;
	}

	public String provider() {
		return this.provider;
	}

	public String targetDid() {
		return this.targetDid;
	}

	public String targetHost() {
		return this.targetHost;
	}

	public String storePath() {
		return this.storePath;
	}

	public static NodeConfig deserialize(String content) {
		return deserialize(content, NodeConfig.class);
	}
}
