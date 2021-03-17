package org.elastos.hive.auth;

import org.elastos.hive.connection.model.AuthToken;
import org.elastos.hive.exception.HiveException;
import org.jetbrains.annotations.NotNull;

public interface TokenResolver {
	@NotNull
	AuthToken getToken() throws HiveException;
	void saveToken() throws HiveException;
	void setNextResolver(TokenResolver resolver);
}
