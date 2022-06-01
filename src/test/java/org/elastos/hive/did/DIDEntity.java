package org.elastos.hive.did;

import org.elastos.did.DID;
import org.elastos.did.DIDDocument;
import org.elastos.did.DIDStore;
import org.elastos.did.RootIdentity;
import org.elastos.did.exception.DIDException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

class DIDEntity {
	private static final Logger log = LoggerFactory.getLogger(DIDEntity.class);

	private final String name;
	private final String phrasepass;
	protected final String storepass;

	private DIDStore didStore;
	private DID did;

	protected DIDEntity(String name, String mnemonic, String phrasepass, String storepass,
						boolean needResolve, String network) throws DIDException {
		this.name = name;
		this.phrasepass = phrasepass;
		this.storepass = storepass;
		this.initDid(mnemonic, needResolve, network);
	}

	private void initDid(String mnemonic, boolean needResolve, String network) throws DIDException {
		final String storePath = System.getProperty("user.dir") + File.separator +
				"data/" + network + "/didCache" + File.separator + name;
		didStore = DIDStore.open(storePath);
		RootIdentity rootIdentity = this.getRootIdentity(mnemonic);
		this.initDidByRootIdentity(rootIdentity, needResolve);
	}

	private RootIdentity getRootIdentity(String mnemonic) throws DIDException {
		String id = RootIdentity.getId(mnemonic, phrasepass);
		return didStore.containsRootIdentity(id) ? didStore.loadRootIdentity(id)
				: RootIdentity.create(mnemonic, phrasepass, didStore, storepass);
	}

	private void initDidByRootIdentity(RootIdentity rootIdentity, boolean needResolve) throws DIDException {
		List<DID> dids = didStore.listDids();
		if (dids.size() > 0) {
			this.did = dids.get(0);
		} else {
			if (needResolve) {
				// Sync the all information of the did with index 0.
				boolean synced = rootIdentity.synchronize(0);
				log.info("{}: identity synchronized result: {}", this.name, synced);
				this.did = rootIdentity.getDid(0);
			} else {
				// Only create the did on local store.
				DIDDocument doc = rootIdentity.newDid(storepass);
				this.did = doc.getSubject();
				System.out.format("[%s] My new DID created: %s%n", name, did);
			}
		}
		if (this.did == null) {
			throw new DIDException("Can not get the did from the local store.");
		}
	}

	protected DIDStore getDIDStore() {
		return didStore;
	}

	public DID getDid() {
		return did;
	}

	public DIDDocument getDocument() throws DIDException {
		return didStore.loadDid(did);
	}

	public String getName() {
		return name;
	}

	protected String getStorePassword() {
		return storepass;
	}

	@Override
	public String toString() {
		return this.did.toString();
	}
}
