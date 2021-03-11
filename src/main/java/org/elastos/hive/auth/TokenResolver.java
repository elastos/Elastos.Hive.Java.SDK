package org.elastos.hive.auth;

import org.elastos.hive.AuthToken;

public interface TokenResolver {
	public AuthToken getToken();
	public void saveToken();
	public void setNextResolver(TokenResolver resolver);
}
