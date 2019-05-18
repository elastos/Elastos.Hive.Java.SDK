package org.elastos.hive.vendors.hiveIpfs;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.ClientInfo;
import org.elastos.hive.Drive;
import org.elastos.hive.DriveType;
import org.elastos.hive.HiveException;
import org.elastos.hive.NullCallback;

public final class HiveIpfsClient extends Client {
	private static Client clientInstance;
	private ClientInfo clientInfo;

	private HiveIpfsClient(HiveIpfsParameter parameter) {
		// TODO;
	}

	public static Client createInstance(HiveIpfsParameter parameter) {
		if (clientInstance == null) {
			clientInstance = new HiveIpfsClient(parameter);
		}
		return clientInstance;
	}

	public static Client getInstance() {
		return clientInstance;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public DriveType getDriveType() {
		return DriveType.hiveIpfs;
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
		return clientInfo;
	}

	@Override
	public CompletableFuture<ClientInfo> getInfo() {
		return getInfo(new NullCallback<ClientInfo>());
	}

	@Override
	public CompletableFuture<ClientInfo> getInfo(Callback<ClientInfo> callback) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Drive> getDefaultDrive() {
		return getDefaultDrive(new NullCallback<Drive>());
	}

	@Override
	public CompletableFuture<Drive> getDefaultDrive(Callback<Drive> callback) {
		// TODO
		return null;
	}
}
