package org.elastos.hive.auth;

import org.elastos.hive.exception.HiveException;

public interface TokenResolver {
	AuthToken getToken() throws HiveException;
	void invlidateToken();
	void setNextResolver(TokenResolver resolver);
}
