package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

interface AuthenticationAdapter {

	CompletableFuture<String> getAuthorization(HiveContext context, String jwtToken);
}