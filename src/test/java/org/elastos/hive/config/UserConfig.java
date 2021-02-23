package org.elastos.hive.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserConfig extends Config {
	@JsonProperty("userDid")
	private String userDid;
	@JsonProperty("userName")
	private String userName;
	@JsonProperty("userMnemonic")
	private String userMnemonic;
	@JsonProperty("userPassPhrase")
	private String userPassPhrase;
	@JsonProperty("userStorePass")
	private String userStorePass;

	public String userDid() {
		return this.userDid;
	}

	public String userName() {
		return this.userName;
	}

	public String userMnemonic() {
		return this.userMnemonic;
	}

	public String userPassPhrase() {
		return this.userPassPhrase;
	}

	public String userStorePass() {
		return this.userStorePass;
	}

	public static UserConfig deserialize(String content) {
		return deserialize(content, UserConfig.class);
	}
}
