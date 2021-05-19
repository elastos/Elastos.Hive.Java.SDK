package org.elastos.hive.vault.files;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class FileInfo {
	@SerializedName("type")
	private String type;

	@SerializedName("name")
	private String name;

	@SerializedName("size")
	private int size;

	@SerializedName("last_modify")
	private long updated;

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public int getSize() {
		return size;
	}

	/*
	public String getLastModified() {
		long timeStamp = BigDecimal.valueOf(lastModify).multiply(new BigDecimal(1000)).longValue();
		return DateUtil.getCurrentEpochTimeStamp(timeStamp);
	}
	*/

	public Date getUpdated() {
		// TODO:
		return null;
	}
}
