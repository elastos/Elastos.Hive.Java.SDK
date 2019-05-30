package org.elastos.hive;

public class DirectoryInfo implements ResultItem {
	private final String itemId;

	public DirectoryInfo(String itemId) {
		this.itemId = itemId;
	}

	public String getId() {
		return itemId;
	}
}
