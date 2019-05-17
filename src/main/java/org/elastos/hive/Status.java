package org.elastos.hive;

public class Status implements ResultItem {
	private final int status;

	public Status(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
}
