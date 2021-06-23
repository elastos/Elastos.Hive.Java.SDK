package org.elastos.hive.didhelper;

import java.io.File;
import java.util.List;

import org.elastos.did.DID;
import org.elastos.did.DIDDocument;
import org.elastos.did.DIDStore;
import org.elastos.did.RootIdentity;
import org.elastos.did.exception.DIDException;

class Entity {
	private String phrasepass;
	private RootIdentity identity;
	protected String storepass;

	private String name;
	private DIDStore store;
	private DID did;

	protected Entity(String name, String mnemonic, String phrasepass, String storepass) throws DIDException {
		this.phrasepass = phrasepass;
		this.storepass = storepass;
		this.name = name;

		initPrivateIdentity(mnemonic);
		initDid();
	}

	protected void initPrivateIdentity(String mnemonic) throws DIDException {
		final String storePath = System.getProperty("user.dir") + File.separator + "didCache" + File.separator + name;

		store = DIDStore.open(storePath);

        String id = RootIdentity.getId(mnemonic, phrasepass);
		if (store.containsRootIdentity(id))
			identity = store.loadRootIdentity(id);// Already exists
		else
			identity = RootIdentity.create(mnemonic, phrasepass, store, storepass);
	}

	protected void initDid() throws DIDException {
		List<DID> dids = store.listDids();
		if (dids.size() > 0) {
			for (DID did : dids) {
				if (did.getMetadata().getAlias().equals("me")) {
					System.out.format("[%s] My DID: %s%n", name, did);
					if (store.containsPrivateKeys(did)) {
						this.did = did;
						return;
					}
				}
			}
		}

		DIDDocument doc = identity.newDid(storepass);
		doc.getMetadata().setAlias("me");
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
