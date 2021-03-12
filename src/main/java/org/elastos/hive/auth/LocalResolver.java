package org.elastos.hive.auth;

import org.elastos.hive.AuthToken;
import org.elastos.hive.exception.HiveException;
import org.jetbrains.annotations.NotNull;

public class LocalResolver implements TokenResolver {
	public LocalResolver(String providerAddress) {}

	@NotNull
	@Override
	public AuthToken getToken() throws HiveException {
		return null;
	}

	@Override
	public void saveToken() {
	}

	@Override
	public void setNextResolver(TokenResolver resolver) {

	}
}
