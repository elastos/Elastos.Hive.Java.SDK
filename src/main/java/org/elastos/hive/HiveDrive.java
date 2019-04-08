package org.elastos.hive;

import org.elastos.hive.exceptions.HiveException;
import org.elastos.hive.vendors.dropbox.DropboxDrive;
import org.elastos.hive.vendors.hiveIpfs.HiveIpfsDrive;
import org.elastos.hive.vendors.onedrive.OneDrive;
import org.elastos.hive.vendors.webdav.OwnCloudDrive;
import org.jetbrains.annotations.NotNull;

public abstract class HiveDrive {
	/**
	 * Create an instance with specific options.
	 *
	 * @param options TODO
	 * @return An new drive instance.
	 */
	public static HiveDrive createInstance(@NotNull DriveParameters parameters) {
		switch (parameters.getDriveType()) {
		case oneDrive:
			return OneDrive.createInstance(parameters);

		case dropBox:
			return DropboxDrive.createInstance(parameters);

		case ownCloud:
			return OwnCloudDrive.createInstance(parameters);

		case hiveIpfs:
			return HiveIpfsDrive.createInstance(parameters);

		default:
			return null;
		}
	}

	/**
	 * Get an instance of specific drive type.
	 *
	 * @param driveType The drive type
	 * @return An drive instance.
	 */
	public static HiveDrive getInstance(DriveType driveType) {
		switch(driveType) {
		case oneDrive:
			return OneDrive.getInstance();

		case dropBox:
			return DropboxDrive.getInstance();

		case ownCloud:
			return OwnCloudDrive.getInstance();

		case hiveIpfs:
			return HiveIpfsDrive.getInstance();

		default:
			return null;

		}
	}

	protected abstract HiveFile createFile(@NotNull String path) throws HiveException;

	protected abstract AuthHelper getAuthHelper();

	/**
	 * Login onto OneDrive to get authorization and authentication.
	 *
	 * @return TODO
	 * @throws Exception TODO
	 */

	public abstract boolean login(Authenticator authenticator) throws HiveException;

	/**
	 * Get drive type.
	 *
	 * @return The drive type.
	 */
	public abstract DriveType getDriveType();

	/**
	 * Get the root Hive file object.
	 *
	 * @return The hive file.
	 * @throws Exception The exception
	 */
	@NotNull
	public abstract HiveFile getRootDir() throws HiveException;

	/**
	 * Get the specific hive file object.
	 *
	 * @param pathName The target pathname to acquire.
	 * @return The hive file.
	 * @throws Exception The exception.
	 */
	@NotNull
	public abstract HiveFile getFile(@NotNull String pathName) throws HiveException;

	/**
	 *
	 * @return True or false.
	 */
	public boolean someMethod() {
		return true;
	}
}
