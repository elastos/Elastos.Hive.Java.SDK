package org.elastos.hive;

public class DriveInfo implements ResultItem {
	private final String driveId;

	public DriveInfo(String driveId) {
		this.driveId = driveId;
	}

	public String getId() {
		return driveId;
	}
}
