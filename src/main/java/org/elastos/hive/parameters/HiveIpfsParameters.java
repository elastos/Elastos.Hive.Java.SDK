package org.elastos.hive.parameters;

import org.elastos.hive.DriveParameters;
import org.elastos.hive.DriveType;

public final class HiveIpfsParameters extends DriveParameters {

	@Override
	protected DriveType getDriveType() {
		return DriveType.hiveIpfs;
	}
}
