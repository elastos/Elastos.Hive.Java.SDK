package org.elastos.hive.vendors.ipfs;

class IPFSEntry {
	private String uid;
	private final String dataPath;
	private final String[] rpcIPAddrs;

	IPFSEntry(String uid, String[] rpcIPAddrs, String dataPath) {
		this.uid = uid;
		this.dataPath = dataPath;
		this.rpcIPAddrs = rpcIPAddrs;
	}

	String getUid() {
		return this.uid;
	}

	void setUid(String uid) {
		this.uid = uid;
	}

	String getDataPath() {
		return this.dataPath;
	}

	String[] getRpcIPAddrs() {
		return this.rpcIPAddrs;
	}
}
