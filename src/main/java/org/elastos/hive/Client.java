package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.vendors.dropbox.DropboxClient;
import org.elastos.hive.vendors.dropbox.DropboxParameter;
import org.elastos.hive.vendors.hiveIpfs.HiveIpfsClient;
import org.elastos.hive.vendors.hiveIpfs.HiveIpfsParameter;
import org.elastos.hive.vendors.onedrive.OneDriveClient;
import org.elastos.hive.vendors.onedrive.OneDriveParameter;
import org.elastos.hive.vendors.webdav.OwnCloudClient;
import org.elastos.hive.vendors.webdav.OwnCloudParameter;

public abstract class Client implements ResourceItem<ClientInfo> {
	public static <T> Client createInstance(Parameter<T> parameter) {
		if (parameter == null)
			return null;

		switch (parameter.getDriveType()) {
		case oneDrive:
			return OneDriveClient.createInstance((OneDriveParameter)parameter);

		case dropbox:
			return DropboxClient.createInstance((DropboxParameter)parameter);

		case hiveIpfs:
			return HiveIpfsClient.createInstance((HiveIpfsParameter)parameter);

		case ownCloud:
			return OwnCloudClient.createInstance((OwnCloudParameter)parameter);

		default:
			break;
		}
		return null;
	}

	public static Client getInstance(DriveType driveType) {

		switch (driveType) {
		case oneDrive:
			return OneDriveClient.getInstance();

		case dropbox:
			return DropboxClient.getInstance();

		case hiveIpfs:
			return HiveIpfsClient.getInstance();

		case ownCloud:
			return OwnCloudClient.getInstance();

		default:
			break;
		}
		return null;
	}

	public abstract DriveType getDriveType();

	public abstract void login(Authenticator authenticator) throws HiveException;
	public abstract void logout() throws HiveException;

	@Override
	public abstract ClientInfo getLastInfo();

	@Override
	public abstract CompletableFuture<ClientInfo> getInfo();
	@Override
	public abstract CompletableFuture<ClientInfo> getInfo(Callback<ClientInfo> callback);

	public abstract CompletableFuture<Drive> getDefaultDrive();
	public abstract CompletableFuture<Drive> getDefaultDrive(Callback<Drive> callback);
}
