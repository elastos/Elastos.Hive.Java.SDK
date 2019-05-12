package org.elastos.hive;

public enum DriveType {
	localDrive("Local Drive"), oneDrive("OneDrive"), dropbox("Dropbox"), hiveIpfs("Hive IPFS"), ownCloud("ownCloud");

	private final String name;

	private DriveType(final String name) {
		this.name = name;
	}
  
	@Override
	public String toString() {
		return name;
	}
}
