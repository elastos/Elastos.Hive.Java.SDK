package org.elastos.hive;

public class DirectoryInfo implements BaseItem {
	private final String dirId;

	public DirectoryInfo(final String dirId) {
		this.dirId = dirId;
	}

	@Override
	public String getId() {
		return dirId;
	}
}
