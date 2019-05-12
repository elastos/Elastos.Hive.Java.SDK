package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.vendors.dropbox.DropboxClient;
import org.elastos.hive.vendors.hiveIpfs.HiveIpfsClient;
import org.elastos.hive.vendors.onedrive.OneDriveClient;
import org.elastos.hive.vendors.webdav.OwnCloudClient;

public abstract class HiveClient implements HiveItem {
	public static <T> HiveClient createInstance(Parameter<T> parameter) {
		if (parameter == null)
			return null;

		switch (parameter.getDriveType()) {
		case oneDrive:
			return OneDriveClient.createInstance(parameter);

		case dropbox:
			return DropboxClient.createInstance(parameter);

		case hiveIpfs:
			return HiveIpfsClient.createInstance(parameter);

		case ownCloud:
			return OwnCloudClient.createInstance(parameter);

		default:
			break;
		}
		return null;
	}

	public static HiveClient getInstance(DriveType driveType) {

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

	public abstract ClientInfo getLastInfo();

	public abstract CompletableFuture<HiveResult<ClientInfo>> getInfo();
	public abstract CompletableFuture<HiveResult<ClientInfo>> getInfo(HiveCallback<ClientInfo, HiveException> callback);

	public abstract CompletableFuture<HiveResult<HiveDrive>> getDefaultDrive();
	public abstract CompletableFuture<HiveResult<HiveDrive>> getDefaultDrive(HiveCallback<HiveDrive, HiveException> callback);
}
