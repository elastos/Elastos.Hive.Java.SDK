package org.elastos.hive;

public class IPFSEntry {
	private final String uid;
	private final String[] rpcAddrs;

	public IPFSEntry(String uid, String[] rpcAddrs) {
		this.uid = uid;
		this.rpcAddrs = rpcAddrs;
	}

	public String getUid() {
		return uid;
	}

	public String[] getRcpAddrs() {
		return rpcAddrs;
	}
}
