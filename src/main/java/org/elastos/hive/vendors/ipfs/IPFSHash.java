package org.elastos.hive.vendors.ipfs;

import org.elastos.hive.Result;

class IPFSHash extends Result {
	private final String hashValue;

	IPFSHash(String hash) {
		this.hashValue = hash;
	}

	String getValue() {
		return hashValue;
	}
}
