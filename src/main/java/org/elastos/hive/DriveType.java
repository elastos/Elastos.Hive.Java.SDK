package org.elastos.hive;

public enum DriveType {
	localDrive("Local Drive"),
	oneDrive("OneDrive"),
	dropbox("Dropbox"),
	hiveIpfs("Hive IPFS"),
	ownCloud("ownCloud");

	private final String driveName;

	private DriveType(final String driveName) {
		this.driveName = driveName;
	}

	@Override
	public String toString() {
		return driveName;
	}
}
