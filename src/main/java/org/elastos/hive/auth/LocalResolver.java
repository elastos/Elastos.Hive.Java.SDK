package org.elastos.hive.auth;

import org.elastos.hive.connection.model.AuthToken;
import org.elastos.hive.exception.HiveException;
import org.jetbrains.annotations.NotNull;

public class LocalResolver implements TokenResolver {
	private TokenResolver nextResolver;

	public LocalResolver(String providerAddress) {}

	@NotNull
	@Override
	public AuthToken getToken() throws HiveException {
		return nextResolver.getToken();
	}

	@Override
	public void saveToken() {
	}

	@Override
	public void setNextResolver(TokenResolver resolver) {
		this.nextResolver = resolver;
	}
}
