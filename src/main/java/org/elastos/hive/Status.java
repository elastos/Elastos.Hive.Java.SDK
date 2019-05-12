package org.elastos.hive;

public class Status implements HiveItem {
	private final String ID = "Status";
	private final int status;

	public Status(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	@Override
	public String getId() {
		return ID;
	}
}
