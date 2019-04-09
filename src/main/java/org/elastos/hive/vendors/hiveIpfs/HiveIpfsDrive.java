package org.elastos.hive.vendors.hiveIpfs;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.Authenticator;
import org.elastos.hive.DriveParameters;
import org.elastos.hive.DriveType;
import org.elastos.hive.HiveDrive;
import org.elastos.hive.HiveFile;
import org.elastos.hive.exceptions.HiveException;
import org.jetbrains.annotations.NotNull;

public final class HiveIpfsDrive extends HiveDrive {
	private static HiveIpfsDrive hiveIpfsInstance;

	private HiveIpfsDrive(DriveParameters parameters) {
		super();
		// TODO;
	}

	public static HiveIpfsDrive createInstance(DriveParameters parameters) {
		if (hiveIpfsInstance == null) {
			hiveIpfsInstance = new HiveIpfsDrive(parameters);
		}

		return hiveIpfsInstance;
	}

	public static HiveIpfsDrive getInstance() {
		return hiveIpfsInstance;
	}

	@Override
	protected AuthHelper getAuthHelper() {
		// TODO
		return null;
	}

	@Override
	public DriveType getDriveType() {
		return DriveType.hiveIpfs;
	}

	@Override
	public boolean login(Authenticator authenticator) throws HiveException {
		// TODO
		return false;
	}

	@Override
	public void logout() {
		// TODO
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
	@NotNull
	protected HiveFile createFile(@NotNull String path) throws HiveException {
		// TODO
		return null;
	}
}
