package org.elastos.hive.auth;

import org.elastos.hive.AuthToken;
import org.elastos.hive.exception.HiveException;
import org.jetbrains.annotations.NotNull;

public interface TokenResolver {
	@NotNull
	AuthToken getToken() throws HiveException;
	void saveToken();
	void setNextResolver(TokenResolver resolver);
}
