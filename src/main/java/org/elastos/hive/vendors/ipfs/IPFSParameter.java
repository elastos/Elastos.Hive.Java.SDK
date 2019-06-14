package org.elastos.hive.vendors.ipfs;

import org.elastos.hive.DriveType;
import org.elastos.hive.NullEntry;
import org.elastos.hive.Parameter;

public class IPFSParameter implements Parameter<NullEntry> {
	private final IPFSEntry ipfsEntry;
	public IPFSParameter(String uid, String[] rpcAddrs, String dataPath) {
		ipfsEntry = new IPFSEntry(uid, rpcAddrs, dataPath);
	}

	IPFSEntry getIpfsEntry() {
		return ipfsEntry;
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
