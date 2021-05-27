package org.elastos.hive.vault.files;

import com.google.gson.annotations.SerializedName;

/**
 * The class to represent the information of File or Folder.
 */
public class FileInfo {
	@SerializedName("name")
	private String name;
	@SerializedName("is_file")
	private boolean isFile;
	@SerializedName("size")
	private int size;
	@SerializedName("created")
	private long created;
	@SerializedName("updated")
	private long updated;

	public String getName() {
		return name;
	}

	public boolean isFile() {
		return isFile;
	}

	public int getSize() {
		return size;
	}

	public long getCreated() {
		return created;
	}

	public long getUpdated() {
		return updated;
	}
}
