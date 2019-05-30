package org.elastos.hive;

public class DirectoryInfo implements ResultItem {
	private final String itemId;
	private String pathName;

	public DirectoryInfo(String itemId) {
		this.itemId = itemId;
	}

	public String getId() {
		return itemId;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public String getPathName() {
		return pathName;
	}
}
