package org.elastos.hive;

public class DriveInfo implements BaseItem {
	private final String driveId;

	public DriveInfo(String driveId) {
		this.driveId = driveId;
	}

	@Override
	public String getId() {
		return driveId;
	}
}
