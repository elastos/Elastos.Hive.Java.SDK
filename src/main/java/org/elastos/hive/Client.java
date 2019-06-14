package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.vendors.dropbox.DropboxClient;
import org.elastos.hive.vendors.dropbox.DropboxParameter;
import org.elastos.hive.vendors.ipfs.IPFSClient;
import org.elastos.hive.vendors.ipfs.IPFSParameter;
import org.elastos.hive.vendors.onedrive.OneDriveClient;
import org.elastos.hive.vendors.onedrive.OneDriveParameter;
import org.elastos.hive.vendors.webdav.OwnCloudClient;
import org.elastos.hive.vendors.webdav.OwnCloudParameter;

public abstract class Client implements ResourceItem<Client.Info> {
	public static class Info implements ResultItem{
		private final String userId;
		private String displayName;
		private String email;
		private String phoneNo;
		private String region;

		public Info(String userId) {
			this.userId = userId;
		}

		public String getUserId() {
			return userId;
		}

		public String getDisplayName() {
			return displayName;
		}

		public String email() {
			return email;
		}

		public String getPhoneNo() {
			return phoneNo;
		}

		public String getRegion() {
			return region;
		}

		public Info setDisplayName(String displayName) {
			this.displayName = displayName;
			return this;
		}

		public Info setEmail(String email) {
			this.email = email;
			return this;
		}

		public Info setPhoneNo(String phoneNo) {
			this.phoneNo = phoneNo;
			return this;
		}

		public Info setRegion(String region) {
			this.region = region;
			return this;
		}
	}

	public static <T> Client createInstance(Parameter<T> parameter) throws HiveException {
		if (parameter == null)
			return null;

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
