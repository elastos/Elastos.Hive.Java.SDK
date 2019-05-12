package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface AuthHelper {
	public AuthToken getAuthToken();

	public CompletableFuture<Result<AuthToken>> loginAsync(Authenticator authenticator);
	public CompletableFuture<Result<AuthToken>> loginAsync(Authenticator authenticator, Callback<AuthToken> callback);

	public CompletableFuture<Result<Status>>    logoutAsync(Callback<Status> callback);
	public CompletableFuture<Result<Status>>    logoutAsync();

	public CompletableFuture<Result<AuthToken>> checkExpired(Callback<AuthToken> callback);
}
