package org.elastos.hive.vendors.hiveIpfs;

import org.elastos.hive.DriveType;
import org.elastos.hive.NullEntry;
import org.elastos.hive.Parameter;

public class HiveIpfsParameter implements Parameter<NullEntry> {
	private String uid;
	public HiveIpfsParameter(String uid) {
		this.uid = uid;
	}
	
	String getUid() {
		return uid;
	}
	
	void setUid(String uid) {
		this.uid = uid;
	}
	
	@Override
	public DriveType getDriveType() {
		return DriveType.hiveIpfs;
	}

	@Override
	public NullEntry getAuthEntry() {
		return null;
	}
}
