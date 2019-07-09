/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.hive;

import org.elastos.hive.vendors.dropbox.DropboxClient;
import org.elastos.hive.vendors.dropbox.DropboxParameter;
import org.elastos.hive.vendors.ipfs.IPFSClient;
import org.elastos.hive.vendors.ipfs.IPFSParameter;
import org.elastos.hive.vendors.onedrive.OneDriveClient;
import org.elastos.hive.vendors.onedrive.OneDriveParameter;
import org.elastos.hive.vendors.webdav.OwnCloudClient;
import org.elastos.hive.vendors.webdav.OwnCloudParameter;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * Hive Client <br>
 * All other hive APIs should be called after having client instance.<br>
 */
public abstract class Client extends Result implements ResourceItem<Client.Info> {
	protected Client(){
	}

	/**
	 * Associated user's information<br>
	 * The result is filled into Client.Info<br>
	 */
	public static class Info extends AttributeMap {
		/**
		 * User Id.
		 */
		public static final String userId = "UserId";

		/**
		 * User's display name.
		 */
		public static final String name   = "DisplayName";

		/**
		 *User's email address.
		 */
		public static final String email  = "Email";

		/**
		 * User's phone number.
		 */
		public static final String phoneNo= "PhoneNo";

		/**
		 * User's region.
		 */
		public static final String region = "Region";

		/**
		 * Info constructor
		 * @param hash The map with the 'userId','name', 'email', 'phoneNo' and 'region' key-value
		 */
		public Info(HashMap<String, String> hash) {
			super(hash);
		}
	}

	/**
	 * Create a new hive client instance to the specific backend.<br>
	 * @param parameter Set up the parameters required by the singleton client。<p>
	 *                  Contains:`DropboxParameter`, `OwnCloudParameter`, 'IPFSParameter' and 'OneDriveParameter',<br>
	 *                  For example: Use a OneDriveParameter will create OneDriveClient singleton
	 * @param <T> Client type .<br>
	 *           Contains : `OneDriveClient`, `IPFSClient`, 'DropboxClient' and 'OwnCloudClient'<br>
	 * @return Return a Hive Client singleton
	 * @throws HiveException An exception is thrown if an error occurs during createInstance
	 */
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

	/**
	 * Get a Hive Client sigleton based on the parameters passed in
	 * @param driveType {@link DriveType} paremeter<br>
	 *                  <br>
	 *                  There are several types：<br>
	 *                  @see DriveType <br>
	 *                  <br>
	 *                  localDrive("Local Drive"),<br>
	 *                  oneDrive("OneDrive"),<br>
	 *                  dropbox("Dropbox"),<br>
	 *                  hiveIpfs("Hive IPFS"),<br>
	 *                  ownCloud("ownCloud");<br>
	 * @return Return a HiveClient singleton
	 */
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

	/**
	 * Get current Hive Client driveType.
	 * @return Return current Hive Client driveType.
	 */
	public abstract DriveType getDriveType();

	/**
	 * Associate a user with the {@link Client}. During the process, the user delegates to the {@link Client}.<br>
	 * <br>
	 * This function is effective only when state of {@link Client} is "non logined state".<br>
	 * <br>
	 * @param authenticator {@link Authenticator} instance
	 * @throws HiveException An exception is thrown if an error occurs during login
	 */
	public abstract void login(Authenticator authenticator) throws HiveException;

	/**
	 * Dissociate the user from the {@link Client}. All client's data in persistent
	 * location is deleted. All derived instances of this client, such as drive
	 * and file instances would become invalid. And any calling APIs with invalid
	 * drive and file instance would be undefined.<br>
	 * <br>
	 * This function is effective only when state of {@link Client} is "logined".<br>
	 * <br>
	 * @throws HiveException An exception is thrown if an error occurs during logout<br>
	 */
	public abstract void logout() throws HiveException;

	/**
	 * Get last {@link Client.Info} associated user's information.
	 * @return Return last {@link Client.Info}
	 */
	@Override
	public abstract Client.Info getLastInfo();

	/**
	 * Get {@link Client.Info} associated user's information. The result is filled into {@link Client.Info}.<br>
	 * <br>
	 * This function is effective only when state of {@link Client} is "logined".<br>
	 * <br>
	 * @return If no error occurs , return current {@link Client.Info}
	 */
	@Override
	public abstract CompletableFuture<Client.Info> getInfo();

	/**
	 * Get {@link Client.Info} associated user's information. The result is filled into {@link Client.Info}.
	 * <br>
	 * This function is effective only when state of {@link Client} is "logined".<br>
	 * <br>
	 * @param callback callback user's information result
	 * @return If no error occurs , return current {@link Client.Info}
	 */
	@Override
	public abstract CompletableFuture<Client.Info> getInfo(Callback<Client.Info> callback);

	/**
	 * Get current Client's {@link Drive},return current backend's {@link Drive} instance.
	 * @return If no error occurs , return current backend's {@link Drive}
	 */
	public abstract CompletableFuture<Drive> getDefaultDrive();

	/**
	 * Get current Client's {@link Drive},return current backend's {@link Drive} instance.
	 * @param callback callback getDefaultDrive result
	 * @return If no error occurs , return current backend's {@link Drive}
	 */
	public abstract CompletableFuture<Drive> getDefaultDrive(Callback<Drive> callback);

}
