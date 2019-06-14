package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface AuthHelper {
	AuthToken getToken();

	CompletableFuture<Status> 	loginAsync(Authenticator authenticator);
	CompletableFuture<Status> 	loginAsync(Authenticator authenticator, Callback<Status> callback);

	CompletableFuture<Status>	logoutAsync();
	CompletableFuture<Status>    logoutAsync(Callback<Status> callback);

	CompletableFuture<Status> 	checkExpired();
	CompletableFuture<Status> 	checkExpired(Callback<Status> callback);
}
