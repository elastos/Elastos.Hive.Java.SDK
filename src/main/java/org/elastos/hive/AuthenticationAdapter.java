package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

interface AuthenticationAdapter {

	CompletableFuture<String> getAuthorization(ApplicationContext context, String jwtToken);
}