package org.elastos.hive.service;

import java.util.concurrent.CompletableFuture;

public interface BackupContext {
	public String getType();
	public String getParamater(String parameter);
	public CompletableFuture<String> getAuthorization(String request);
}
