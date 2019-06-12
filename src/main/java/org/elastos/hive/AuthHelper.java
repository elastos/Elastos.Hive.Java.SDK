package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface AuthHelper {
	public AuthToken getToken();

	public CompletableFuture<Status> 	loginAsync(Authenticator authenticator);
	public CompletableFuture<Status> 	loginAsync(Authenticator authenticator, Callback<Status> callback);

	public CompletableFuture<Status>	logoutAsync();
	public CompletableFuture<Status>    logoutAsync(Callback<Status> callback);

	public CompletableFuture<Status> 	checkExpired();
	public CompletableFuture<Status> 	checkExpired(Callback<Status> callback);
}
