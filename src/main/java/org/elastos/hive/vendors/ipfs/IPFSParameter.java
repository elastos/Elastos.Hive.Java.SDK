package org.elastos.hive.vendors.ipfs;

import org.elastos.hive.DriveType;
import org.elastos.hive.IPFSEntry;
import org.elastos.hive.Parameter;

public class IPFSParameter implements Parameter<IPFSEntry> {
	private final IPFSEntry entry;
	private final String keyStorePath;

	public IPFSParameter(IPFSEntry entry, String storePath)  {
		this.entry = entry;
		this.keyStorePath = storePath;
	}

	@Override
	public DriveType getDriveType() {
		return DriveType.hiveIpfs;
	}

	@Override
	public IPFSEntry getAuthEntry() {
		return this.entry;
	}

	@Override
	public String getKeyStorePath() {
		return keyStorePath;
	}
}
