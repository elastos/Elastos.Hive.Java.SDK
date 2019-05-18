package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface AuthHelper {
	public AuthToken getToken();

	public CompletableFuture<AuthToken> loginAsync(Authenticator authenticator);
	public CompletableFuture<AuthToken> loginAsync(Authenticator authenticator, Callback<AuthToken> callback);

    public CompletableFuture<Status>    logoutAsync();
	public CompletableFuture<Status>    logoutAsync(Callback<Status> callback);

	public CompletableFuture<AuthToken> checkExpired(Callback<AuthToken> callback);
}
