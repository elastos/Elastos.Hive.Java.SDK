package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface InternalHandler {

	CompletableFuture<String> authenticate(AuthenticationHandler handler, String jwtToken);
}