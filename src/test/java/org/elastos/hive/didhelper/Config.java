package org.elastos.hive.didhelper;

public class Config {
	private String userDid;
	private String userName;
	private String userMn;
	private String userPhrasepass;
	private String userStorepass;

	private String appName;
	private String appMn;
	private String appPhrasepass;
	private String appStorePass;

	private String storePath;

	private String resolverUrl;
	private String provider;

	public String getUserDid() {
		return userDid;
	}

	public void setUserDid(String userDid) {
		this.userDid = userDid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserMn() {
		return userMn;
	}

	public void setUserMn(String userMn) {
		this.userMn = userMn;
	}

	public String getUserPhrasepass() {
		return userPhrasepass;
	}

	public void setUserPhrasepass(String userPhrasepass) {
		this.userPhrasepass = userPhrasepass;
	}

	public String getUserStorepass() {
		return userStorepass;
	}

	public void setUserStorepass(String userStorepass) {
		this.userStorepass = userStorepass;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppMn() {
		return appMn;
	}

	public void setAppMn(String appMn) {
		this.appMn = appMn;
	}

	public String getAppPhrasepass() {
		return appPhrasepass;
	}

	public void setAppPhrasepass(String appPhrasepass) {
		this.appPhrasepass = appPhrasepass;
	}

	public String getAppStorePass() {
		return appStorePass;
	}

	public void setAppStorePass(String appStorePass) {
		this.appStorePass = appStorePass;
	}

	public String getResolverUrl() {
		return resolverUrl;
	}

	public void setResolverUrl(String resolverUrl) {
		this.resolverUrl = resolverUrl;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getStorePath() {
		return storePath;
	}

	public void setStorePath(String storePath) {
		this.storePath = storePath;
	}
}
