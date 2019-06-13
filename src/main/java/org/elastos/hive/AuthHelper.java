package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface AuthHelper {
	AuthToken getToken();

	CompletableFuture<AuthToken> loginAsync(Authenticator authenticator);
	CompletableFuture<AuthToken> loginAsync(Authenticator authenticator, Callback<AuthToken> callback);

    CompletableFuture<Status>    logoutAsync();
	CompletableFuture<Status>    logoutAsync(Callback<Status> callback);

	CompletableFuture<AuthToken> checkExpired(Callback<AuthToken> callback);
}
