package org.elastos.hive;

public class IPFSEntry {
	private String uid;
	private final String[] rpcAddrs;

	public IPFSEntry(String uid, String[] rpcAddrs) {
		this.uid = uid;
		this.rpcAddrs = rpcAddrs;
	}

	public String setUid(String uid) {
		return this.uid = uid;
	}

	public String getUid() {
		return uid;
	}

	public String[] getRcpAddrs() {
		return rpcAddrs;
	}
}
