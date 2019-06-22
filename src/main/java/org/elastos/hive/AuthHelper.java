package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface AuthHelper {
	AuthToken getToken();

	CompletableFuture<Void> loginAsync(Authenticator authenticator);
	CompletableFuture<Void> loginAsync(Authenticator authenticator, Callback<Void> callback);

	CompletableFuture<Void>	logoutAsync();
	CompletableFuture<Void> logoutAsync(Callback<Void> callback);

	CompletableFuture<Void> checkExpired();
	CompletableFuture<Void> checkExpired(Callback<Void> callback);
}
