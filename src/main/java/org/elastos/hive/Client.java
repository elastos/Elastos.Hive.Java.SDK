package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface Client extends FileItem {
	public void login(Authenticator authenticator) throws HiveException;

	public void logout();

	public HiveDrive getDefaultDrive();

	public CompletableFuture<HiveResult<ClientInfo>> getInfo();
	public CompletableFuture<HiveResult<ClientInfo>> getInfo(Callback<ClientInfo, HiveException> callback);
}
