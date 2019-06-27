package org.elastos.hive;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.vendors.dropbox.DropboxClient;
import org.elastos.hive.vendors.dropbox.DropboxParameter;
import org.elastos.hive.vendors.ipfs.IPFSClient;
import org.elastos.hive.vendors.ipfs.IPFSParameter;
import org.elastos.hive.vendors.onedrive.OneDriveClient;
import org.elastos.hive.vendors.onedrive.OneDriveParameter;
import org.elastos.hive.vendors.webdav.OwnCloudClient;
import org.elastos.hive.vendors.webdav.OwnCloudParameter;

public abstract class Client extends Result implements ResourceItem<Client.Info> {
	public static class Info extends AttributeMap {
		public static final String userId = "UserId";
		public static final String name   = "DisplayName";
		public static final String email  = "Email";
		public static final String phoneNo= "PhoneNo";
		public static final String region = "Region";

		public Info(HashMap<String, String> hash) {
			super(hash);
		}
	}

	public static <T> Client createInstance(Parameter<T> parameter) throws HiveException {
		if (parameter == null)
			throw new HiveException("Null Parameter is not allowed");

		switch (parameter.getDriveType()) {
		case oneDrive:
			return OneDriveClient.createInstance((OneDriveParameter)parameter);

		case dropbox:
			return DropboxClient.createInstance((DropboxParameter)parameter);

		case hiveIpfs:
			return IPFSClient.createInstance((IPFSParameter)parameter);

		case ownCloud:
			return OwnCloudClient.createInstance((OwnCloudParameter)parameter);

		default:
			throw new HiveException("Not supported drive type: " + parameter.getDriveType());
		}
	}

	public static Client getInstance(DriveType driveType) {

		switch (driveType) {
		case oneDrive:
			return OneDriveClient.getInstance();

		case dropbox:
			return DropboxClient.getInstance();

		case hiveIpfs:
			return IPFSClient.getInstance();

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
	public abstract Client.Info getLastInfo();

	@Override
	public abstract CompletableFuture<Client.Info> getInfo();
	@Override
	public abstract CompletableFuture<Client.Info> getInfo(Callback<Client.Info> callback);

	public abstract CompletableFuture<Drive> getDefaultDrive();
	public abstract CompletableFuture<Drive> getDefaultDrive(Callback<Drive> callback);
}
