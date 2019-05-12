package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface AuthHelper {
	public AuthToken getAuthToken();

	public CompletableFuture<HiveResult<AuthToken>> loginAsync(Authenticator authenticator);
	public CompletableFuture<HiveResult<AuthToken>> loginAsync(Authenticator authenticator, HiveCallback<AuthToken, HiveException> callback);

	public CompletableFuture<HiveResult<Status>>    logoutAsync(HiveCallback<Status, HiveException> callback);
	public CompletableFuture<HiveResult<Status>>    logoutAsync();

	public CompletableFuture<HiveResult<AuthToken>> checkExpired(HiveCallback<AuthToken, HiveException> callback);
}
