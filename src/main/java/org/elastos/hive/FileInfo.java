package org.elastos.hive;

public class FileInfo implements HiveItem {
	private final String fileId;

	public FileInfo(String fileId) {
		this.fileId = fileId;
	}

	@Override
	public String getId() {
		return fileId;
	}
}
