package org.elastos.hive;

public class FileInfo implements ResultItem {
	private final String fileId;
	private String pathName;

	public FileInfo(String fileId) {
		this.fileId = fileId;
	}

	public String getId() {
		return fileId;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public String getPathName() {
		return pathName;
	}
}
