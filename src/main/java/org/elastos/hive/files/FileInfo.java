package org.elastos.hive.files;

import java.math.BigDecimal;

import org.elastos.hive.utils.DateUtil;

/**
 * The class to represent the information of File or Folder.
 */
public class FileInfo {
	/**
	 * Type of a remote file or folder.
	 */
	public enum Type {
		/** File */
		FILE,
		/** Folder */
		FOLDER
	};

	private String type;
	private String name;
	private long size;
	private String last_modify;

	public Type getType() {
		switch (type) {
		case "file":
			return Type.FILE;
		case "folder":
			return Type.FOLDER;
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public long getSize() {
		return size;
	}

	public String getLastModified() {
		long timeStamp = new BigDecimal(last_modify).multiply(new BigDecimal(1000)).longValue();
		return DateUtil.getCurrentEpochTimeStamp(timeStamp);
	}
}
