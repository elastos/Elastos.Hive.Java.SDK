package org.elastos.hive.vendors.dropbox;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.Authenticator;
import org.elastos.hive.ClientInfo;
import org.elastos.hive.DriveType;
import org.elastos.hive.HiveCallback;
import org.elastos.hive.HiveClient;
import org.elastos.hive.HiveDrive;
import org.elastos.hive.HiveException;
import org.elastos.hive.HiveResult;

public final class DropboxClient extends HiveClient {
	private static HiveClient clientInstance;

	private DropboxClient(DropboxParameter parameter) {
		// TODO;
	}

	public static HiveClient createInstance(DropboxParameter parameter) {
		if (clientInstance == null) {
			clientInstance = new DropboxClient(parameter);
		}
		return clientInstance;
	}

	public static HiveClient getInstance() {
		return clientInstance;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public DriveType getDriveType() {
		return DriveType.dropbox;
	}

	@Override
	public synchronized void login(Authenticator authenticator) throws HiveException {
		// TODO;
	}

	@Override
	public synchronized void logout() throws HiveException {
		// TODO;
	}

	@Override
	public ClientInfo getLastInfo() {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<ClientInfo>> getInfo() {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<ClientInfo>> getInfo(HiveCallback<ClientInfo, HiveException> callback) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<HiveDrive>> getDefaultDrive() {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<HiveDrive>> getDefaultDrive(HiveCallback<HiveDrive, HiveException> callback) {
		// TODO
		return null;
	}
}
