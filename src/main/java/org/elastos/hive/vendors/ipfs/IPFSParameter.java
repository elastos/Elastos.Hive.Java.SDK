package org.elastos.hive.vendors.ipfs;

import org.elastos.hive.DriveType;
import org.elastos.hive.NullEntry;
import org.elastos.hive.Parameter;

public class IPFSParameter implements Parameter<NullEntry> {
	private String uid;
	private final String dataPath;
	public IPFSParameter(String dataPath, String uid) {
		this.dataPath = dataPath;
		this.uid = uid;
	}

	String getUid() {
		return uid;
	}
	
	void setUid(String uid) {
		this.uid = uid;
	}

	String getDataPath() {
		return this.dataPath;
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
