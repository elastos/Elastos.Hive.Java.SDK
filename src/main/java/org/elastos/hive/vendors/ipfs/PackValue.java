package org.elastos.hive.vendors.ipfs;

import org.elastos.hive.Callback;
import org.elastos.hive.HiveException;
import org.elastos.hive.Result;

class PackValue extends Result {
	private Result value;
	private HiveException exception;
	private Callback<?> callback;
	private IPFSHash hash;

	void setValue(Result value) {
		this.value = value;
	}

	Result getValue() {
		return value;
	}

	void setException(HiveException e) {
		this.exception = e;
	}

	HiveException getException() {
		return exception;
	}

	void setCallback(Callback<?> callback) {
		this.callback = callback;
	}

	Callback<?> getCallback() {
		return callback;
	}

	void setHash(IPFSHash hash) {
		this.hash = hash;
	}

	IPFSHash getHash() {
		return hash;
	}
}
