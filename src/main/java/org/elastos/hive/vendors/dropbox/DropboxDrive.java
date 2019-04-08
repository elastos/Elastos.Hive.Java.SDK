package org.elastos.hive.vendors.dropbox;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.Authenticator;
import org.elastos.hive.DriveParameters;
import org.elastos.hive.DriveType;
import org.elastos.hive.HiveDrive;
import org.elastos.hive.HiveFile;
import org.elastos.hive.exceptions.HiveException;
import org.jetbrains.annotations.NotNull;

public final class DropboxDrive extends HiveDrive {
	private static DropboxDrive dropboxInstance;

	private DropboxDrive(DriveParameters params) {
		super();
		// TODO;
	}

	public static @NotNull DropboxDrive createInstance(@NotNull DriveParameters parameters) {
		if (dropboxInstance == null) {
			dropboxInstance = new DropboxDrive(parameters);
		}

		return dropboxInstance;
	}

	public static DropboxDrive getInstance() {
		return dropboxInstance;
	}

	@Override
	protected AuthHelper getAuthHelper() {
		// TODO;
		return null;
	}

	@Override
	public DriveType getDriveType() {
		return DriveType.dropBox;
	}

	@Override
	public boolean login(Authenticator authenticator) throws HiveException {
		// TODO
		return false;
	}

	@Override
	@NotNull
	public HiveFile getRootDir() throws HiveException {
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
	protected HiveFile createFile(@NotNull String path) throws HiveException {
		// TODO
		return null;
	}
}
