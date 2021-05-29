package org.elastos.hive.vault.files;

import java.util.Date;

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

	public Date getCreated() {
		return new Date(created * 1000);
	}

	public Date getUpdated() {
		return new Date(updated * 1000);
	}
}
