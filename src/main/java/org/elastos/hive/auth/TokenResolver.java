package org.elastos.hive.auth;

import org.elastos.hive.exception.HttpFailedException;

public interface TokenResolver {
	String getToken() throws HttpFailedException;
	void invalidateToken();
	void setNextResolver(TokenResolver resolver);
}
