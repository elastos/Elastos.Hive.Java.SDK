package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface BackupAuthenticationHandler {

	CompletableFuture<String> authorization(String serviceDid, String endPoint);
}
