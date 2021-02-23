package org.elastos.hive.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserConfig extends Config {
	@JsonProperty("did")
	private String did;
	@JsonProperty("name")
	private String name;
	@JsonProperty("mnemonic")
	private String mnemonic;
	@JsonProperty("passPhrase")
	private String passPhrase;
	@JsonProperty("storePass")
	private String storePass;

	public String did() {
		return this.did;
	}

	public String name() {
		return this.name;
	}

	public String mnemonic() {
		return this.mnemonic;
	}

	public String passPhrase() {
		return this.passPhrase;
	}

	public String storePass() {
		return this.storePass;
	}

	public static UserConfig deserialize(String content) {
		return deserialize(content, UserConfig.class);
	}
}
