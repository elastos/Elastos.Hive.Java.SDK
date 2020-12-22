package org.elastos.did;

import org.elastos.did.adapter.DummyAdapter;
import org.elastos.did.exception.DIDException;

import java.io.File;
import java.util.List;

class Entity {
	private String phrasepass;
	protected String storepass;

	private String name;
	private DIDStore store;
	private DID did;

	private static DummyAdapter adapter;

	protected Entity(String name, String mnemonic, DummyAdapter adapter, String phrasepass, String storepass) throws DIDException {
		this.phrasepass = phrasepass;
		this.storepass = storepass;
		this.name = name;
		this.adapter = adapter;

		initPrivateIdentity(mnemonic);
		initDid();
	}

	protected void initPrivateIdentity(String mnemonic) throws DIDException {
		final String storePath = System.getProperty("user.dir") + File.separator + "didCache" + File.separator + name;

		store = DIDStore.open("filesystem", storePath, adapter);

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
