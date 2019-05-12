package org.elastos.hive;

public class OAuthEntry {
	private final String clientId;
	private final String scope;
	private final String redirectURL;

	public OAuthEntry(String clientId, String scope, String redirectURL) {
		this.clientId = clientId;
		this.scope = scope;
		this.redirectURL = redirectURL;
	}

	public String getClientId() {
		return clientId;
	}

	public String getScope() {
		return scope;
	}

	public String getRedirectURL() {
		return redirectURL;
	}
}
