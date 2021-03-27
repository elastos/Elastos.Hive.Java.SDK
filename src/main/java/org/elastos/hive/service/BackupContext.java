package org.elastos.hive.service;

import java.util.concurrent.CompletableFuture;

public interface BackupContext {
	String getType();
	String getParameter(String key);
	CompletableFuture<String> getAuthorization(String request);
}
