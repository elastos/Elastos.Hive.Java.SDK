package org.elastos.did;

import org.elastos.did.adapter.DummyAdapter;
import org.elastos.did.exception.DIDException;
import org.elastos.did.exception.DIDResolveException;
import org.elastos.did.exception.DIDStoreException;

import java.io.File;
import java.util.List;

public class helper {

	/**
	 * Generates a random ID, suitable for DID store ID format.
	 */
	public String generateRandomDIDStoreId() {
		int len = 6;
		int radix = 16;

		char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

		char[] uuid = null;
		if(len > 0) {
			uuid = new char[len];
			for(int i=0; i<len; i++) {
				int index = (int) (Math.random() * radix) | 0;
				uuid[i] = chars[index];
			}
		} else {
			uuid = new char[36];
			// rfc4122, version 4 form
			int r;

			// rfc4122 requires these characters
			uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
			uuid[14] = '4';

			// Fill in random data. At i==19 set the high bits of clock sequence as
			// per rfc4122, sec. 4.1.5
			for (int i=0; i<36; i++) {
				r = (int) (Math.random() * 16) | 0;
				uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
			}
		}

		return new String(uuid);
	}

	public static class FastDIDCreationResult {

		public FastDIDCreationResult(DIDStore didStore, DID did, String storePassword) {
			this.didStore = didStore;
			this.did = did;
			this.storePassword = storePassword;
		}

		public DIDStore didStore() {
			return this.didStore;
		}

		public DID did() {
			return this.did;
		}

		public String storePassword() {
			return this.storePassword;
		}

		private DIDStore didStore;
		private DID did;
		private String storePassword;
	}

	public FastDIDCreationResult fastCreateDID() {
		Mnemonic mg = Mnemonic.getInstance();
		try {
			String mnemonic = mg.generate();
			final String storePath = System.getProperty("java.io.tmpdir")
					+ File.separator + ".store";

			// Create a fake adapter, just print the tx payload to console.
			String storepass = "storepass";
			DIDStore didStore = DIDStore.open("filesystem", storePath, new DummyAdapter());
			didStore.initPrivateIdentity(null, mnemonic, null, storepass);

			DID did = didStore.newDid("me", storepass).getSubject();

			return new FastDIDCreationResult(didStore, did, storepass);
		} catch (DIDException e) {
			e.printStackTrace();
		}

		return null;
	}

	public DID loadDID(DIDStore didStore, String didString) {
		try {
			return didStore.loadDid(didString).getSubject();
		} catch (DIDStoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<VerifiableCredential> loadDIDCredentials(DID did) {
		try {
			return did.resolve().getCredentials();
		} catch (DIDResolveException e) {
			e.printStackTrace();
		}
		return null;
	}
}
