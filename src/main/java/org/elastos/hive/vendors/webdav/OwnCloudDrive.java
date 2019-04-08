/**
 *
 */
package org.elastos.hive.vendors.webdav;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.Authenticator;
import org.elastos.hive.DriveParameters;
import org.elastos.hive.DriveType;
import org.elastos.hive.HiveDrive;
import org.elastos.hive.HiveFile;
import org.elastos.hive.exceptions.HiveException;
import org.jetbrains.annotations.NotNull;

public final class OwnCloudDrive extends HiveDrive {
	private static OwnCloudDrive ownCloudInstance;

	private OwnCloudDrive(DriveParameters parameters) {
		super();
		// TODO
	}

	public static OwnCloudDrive createInstance(DriveParameters parameters) {
		if (ownCloudInstance == null) {
			ownCloudInstance = new OwnCloudDrive(parameters);
		}

		return ownCloudInstance;
	}

	public static OwnCloudDrive getInstance() {
		return ownCloudInstance;
	}

	@Override
	protected AuthHelper getAuthHelper() {
		// TODO
		return null;
	}

	@Override
	public DriveType getDriveType() {
		return DriveType.ownCloud;
	}

	@Override
	public boolean login(Authenticator authenticator) throws HiveException {
		// TODO
		return false;
	}

	@Override
	public @NotNull HiveFile getRootDir() throws HiveException {
		// TODO
		return null;
	}

	@Override
	@NotNull
	public HiveFile getFile(@NotNull String pathName) throws HiveException {
		// TODO
		return null;
	}

	@Override
	@NotNull
	protected HiveFile createFile(@NotNull String path) throws HiveException {
		// TODO
		return null;
	}
}
