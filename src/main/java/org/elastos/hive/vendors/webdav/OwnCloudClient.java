package org.elastos.hive.vendors.webdav;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.Authenticator;
import org.elastos.hive.ClientInfo;
import org.elastos.hive.DriveType;
import org.elastos.hive.HiveCallback;
import org.elastos.hive.HiveClient;
import org.elastos.hive.HiveDrive;
import org.elastos.hive.HiveException;
import org.elastos.hive.HiveResult;

public final class OwnCloudClient extends HiveClient {
	private static HiveClient clientInstance;

	private OwnCloudClient(OwnCloudParameter parameter) {
		// TODO;
	}

	public static HiveClient createInstance(OwnCloudParameter parameter) {
		if (clientInstance == null) {
			clientInstance = new OwnCloudClient(parameter);
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
		return DriveType.ownCloud;
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
