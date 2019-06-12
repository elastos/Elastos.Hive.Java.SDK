package org.elastos.hive.vendors.dropbox;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.Drive;
import org.elastos.hive.DriveType;
import org.elastos.hive.HiveException;
import org.elastos.hive.NullCallback;

public final class DropboxClient extends Client {
	private static Client clientInstance;
	private Client.Info clientInfo;

	private DropboxClient(DropboxParameter parameter) {
		// TODO;
	}

	public static Client createInstance(DropboxParameter parameter) {
		if (clientInstance == null) {
			clientInstance = new DropboxClient(parameter);
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
		return DriveType.dropbox;
	}

	@Override
	public synchronized void login(Authenticator authenticator) throws HiveException {
		throw new HiveException("Not implemented yet");
	}

	@Override
	public synchronized void logout() throws HiveException {
		throw new HiveException("Not implemented yet");
	}

	@Override
	public Client.Info getLastInfo() {
		return clientInfo;
	}

	@Override
	public CompletableFuture<Client.Info> getInfo() {
		return getInfo(new NullCallback<Client.Info>());
	}

	@Override
	public CompletableFuture<Client.Info> getInfo(Callback<Client.Info> callback) {
		if (callback == null)
			callback = new NullCallback<Client.Info>();

		CompletableFuture<Client.Info> future = new CompletableFuture<Client.Info>();
		HiveException e = new HiveException("Not implemented yet");
		callback.onError(e);
		future.completeExceptionally(e);
		return future;
	}

	@Override
	public CompletableFuture<Drive> getDefaultDrive() {
		return  getDefaultDrive(new NullCallback<Drive>());
	}

	@Override
	public CompletableFuture<Drive> getDefaultDrive(Callback<Drive> callback) {
		if (callback == null)
			callback = new NullCallback<Drive>();

		CompletableFuture<Drive> future = new CompletableFuture<Drive>();
		HiveException e = new HiveException("Not implemented yet");
		callback.onError(e);
		future.completeExceptionally(e);
		return future;
	}
}
