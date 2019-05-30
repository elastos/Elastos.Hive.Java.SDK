package org.elastos.hive;

public class FileInfo implements ResultItem {
	private final String fileId;

	public FileInfo(String fileId) {
		this.fileId = fileId;
	}

	public String getId() {
		return fileId;
	}
}
