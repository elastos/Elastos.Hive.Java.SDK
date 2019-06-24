package org.elastos.hive.vendors.onedrive;

import org.elastos.hive.DriveType;
import org.elastos.hive.OAuthEntry;
import org.elastos.hive.Parameter;

public class OneDriveParameter implements Parameter<OAuthEntry> {
	private final OAuthEntry authEntry;
	private final String keyStorePath;

	public OneDriveParameter(OAuthEntry data, String storePath) {
		this.authEntry = data;
		this.keyStorePath = storePath;
	}

	@Override
	public DriveType getDriveType() {
		return DriveType.oneDrive;
	}

	@Override
	public OAuthEntry getAuthEntry() {
		return authEntry;
	}

	@Override
	public String getKeyStorePath() {
		return keyStorePath;
	}
}
