package org.elastos.hive.vendors.dropbox;

import org.elastos.hive.DriveType;
import org.elastos.hive.OAuthEntry;
import org.elastos.hive.Parameter;

public class DropboxParameter implements Parameter<OAuthEntry> {
	private final OAuthEntry authEntry;

	public DropboxParameter(OAuthEntry data) {
		this.authEntry = data;
	}

	@Override
	public DriveType getDriveType() {
		return DriveType.dropbox;
	}

	@Override
	public OAuthEntry getAuthEntry() {
		return authEntry;
	}

	@Override
	public String getKeyStorePath() {
		// TODO Auto-generated method stub
		return null;
	}
}
