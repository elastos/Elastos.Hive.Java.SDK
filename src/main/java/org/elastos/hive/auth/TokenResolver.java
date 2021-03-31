package org.elastos.hive.auth;

public interface TokenResolver {
	AuthToken getToken();
	void invalidateToken();
	void setNextResolver(TokenResolver resolver);
}
