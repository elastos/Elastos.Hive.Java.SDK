package org.elastos.hive.vendors.onedrive;

import org.elastos.hive.DriveType;
import org.elastos.hive.OAuthEntry;
import org.elastos.hive.Parameter;

public class OneDriveParameter implements Parameter<OAuthEntry> {
	private final OAuthEntry authEntry;

	public OneDriveParameter(OAuthEntry data) {
		this.authEntry = data;
	}

	@Override
	public DriveType getDriveType() {
		return DriveType.oneDrive;
	}

	@Override
	public OAuthEntry getAuthEntry() {
		return authEntry;
	}
}
