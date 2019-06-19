package org.elastos.hive.vendors.webdav;

import org.elastos.hive.DriveType;
import org.elastos.hive.NullEntry;
import org.elastos.hive.Parameter;

public final class OwnCloudParameter implements Parameter<NullEntry> {
	@Override
	public DriveType getDriveType() {
		return DriveType.ownCloud;
	}

	@Override
	public NullEntry getAuthEntry() {
		return new NullEntry();
	}

	@Override
	public String getKeyStorePath() {
		// TODO Auto-generated method stub
		return null;
	}
}
