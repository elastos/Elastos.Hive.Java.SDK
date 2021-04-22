package org.elastos.hive.demo.sdk.did;

import org.elastos.did.DID;
import org.elastos.did.DIDDocument;
import org.elastos.did.DIDStore;
import org.elastos.did.adapter.DummyAdapter;
import org.elastos.did.exception.DIDException;

import java.util.List;

class DIDEntity {
	private String phrasepass;
	protected String storepass;

	private String name;
	private DIDStore store;
	private DID did;
	private String storeRootDir;

	private static DummyAdapter adapter;

	protected DIDEntity(String name, String mnemonic, DummyAdapter adapter, String phrasepass, String storepass, String storeRootDir) throws DIDException {
		this.phrasepass = phrasepass;
		this.storepass = storepass;
		this.name = name;
		this.adapter = adapter;
		this.storeRootDir = storeRootDir;

		initPrivateIdentity(mnemonic);
		initDid();
	}

	protected void initPrivateIdentity(String mnemonic) throws DIDException {
		String storeDir = this.storeRootDir + "/" + this.name;
		store = DIDStore.open("filesystem", storeDir, adapter);

		if (store.containsPrivateIdentity())
			return; // Already exists

		store.initPrivateIdentity(null, mnemonic, phrasepass, storepass);
	}

	protected void initDid() throws DIDException {
		List<DID> dids = store.listDids(DIDStore.DID_HAS_PRIVATEKEY);
		if (dids.size() > 0) {
			for (DID did : dids) {
				if (did.getMetadata().getAlias().equals("me")) {
					System.out.format("[%s] My DID: %s%n", name, did);
					this.did = did;
					return;
				}
			}
		}

		DIDDocument doc = store.newDid("me", storepass);
		this.did = doc.getSubject();
		System.out.format("[%s] My new DID created: %s%n", name, did);
	}

	protected DIDStore getDIDStore() {
		return store;
	}

	public DID getDid() {
		return did;
	}

	public DIDDocument getDocument() throws DIDException {
		return store.loadDid(did);
	}

	public String getName() {
		return name;
	}

	protected String getStorePassword() {
		return storepass;
	}
}
