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
