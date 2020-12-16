package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface InternalHandler {

	CompletableFuture<String> authenticate(HiveContext context, String jwtToken);
}