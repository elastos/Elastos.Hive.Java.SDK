/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.hive.vendor.vault;

import org.elastos.did.DIDAdapter;
import org.elastos.did.DIDBackend;
import org.elastos.did.DIDDocument;
import org.elastos.did.DIDResolver;
import org.elastos.did.DIDStore;
import org.elastos.did.DIDURL;
import org.elastos.did.Mnemonic;
import org.elastos.did.adapter.DummyAdapter;
import org.elastos.did.adapter.SPVAdapter;
import org.elastos.did.backend.ResolverCache;
import org.elastos.did.crypto.Base58;
import org.elastos.did.exception.DIDException;
import org.elastos.hive.utils.FileUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public final class DIDData {
	private static DummyAdapter dummyAdapter;
	private static DIDAdapter spvAdapter;

	private DIDAdapter adapter;

	private DIDDocument issuer;

	private DIDDocument didDocument;
	private DIDStore store;

	private VaultOptions options;

	protected static File getResolverCacheDir() {
		return new File(System.getProperty("user.home") +
				File.separator + ".cache.did.elastos");
	}

	public DIDData(VaultOptions options) {
		this.options = options;
	}

	public DIDStore setup(boolean dummyBackend) throws DIDException {
		if (dummyBackend) {
			if (DIDData.dummyAdapter == null)
				DIDData.dummyAdapter = new DummyAdapter(options.verbose());
			else
				DIDData.dummyAdapter.reset();

			adapter = DIDData.dummyAdapter;

			DIDBackend.initialize((DIDResolver)adapter, getResolverCacheDir());
		} else {
			if (DIDData.spvAdapter == null)
				DIDData.spvAdapter = new SPVAdapter(options.walletDir(),
						options.walletId(), options.network(),
						(walletDir, walletId) -> options.walletPassword());

			adapter = DIDData.spvAdapter;

			DIDBackend.initialize(options.resolver(), getResolverCacheDir());
		}

		ResolverCache.reset();
    	FileUtil.deleteFile(new File(options.storeRoot()));
    	store = DIDStore.open("filesystem", options.storeRoot(), adapter);
    	return store;
	}

	public String initIdentity() throws DIDException {
    	String mnemonic =  Mnemonic.getInstance().generate();
    	store.initPrivateIdentity(Mnemonic.ENGLISH, mnemonic,
				options.passphrase(), options.storePass(), true);

    	return mnemonic;
	}

	private DIDDocument loadDIDDocument(String fileName)
			throws DIDException, IOException {
		Reader input = new FileReader(options.storePath() + fileName);
		DIDDocument doc = DIDDocument.fromJson(input);
		input.close();

		if (store != null) {
			store.storeDid(doc);
		}

		return doc;
	}

	private void importPrivateKey(DIDURL id, String fileName)
			throws IOException, DIDException {
		String skBase58 = loadText(fileName);
		byte[] sk = Base58.decode(skBase58);

		store.storePrivateKey(id.getDid(), id, sk, options.storePass());
	}

	public DIDDocument loadIssuer() throws DIDException, IOException {
		if (issuer == null) {
			issuer = loadDIDDocument("issuer.json");

			importPrivateKey(issuer.getDefaultPublicKey(), "issuer.primary.sk");

			store.publishDid(issuer.getSubject(), options.storePass());
		}

		return issuer;
	}

	public DIDDocument loadDocument() throws DIDException, IOException {
		loadIssuer();

		if (didDocument == null) {
			didDocument = loadDIDDocument("document.json");

			importPrivateKey(didDocument.getDefaultPublicKey(), "document.primary.sk");
			importPrivateKey(didDocument.getPublicKey("key2").getId(), "document.key2.sk");
			importPrivateKey(didDocument.getPublicKey("key3").getId(), "document.key3.sk");

			store.publishDid(didDocument.getSubject(), options.storePass());
		}

		return didDocument;
	}

	private String loadText(String fileName) throws IOException {

		FileReader reader = null;
		try {
			reader = new FileReader(options.storePath() + fileName);
			char[] buf = new char[128];
			int len;
			StringBuilder content = new StringBuilder();
			while ((len = reader.read(buf)) != -1) {
				content.append(new String(buf, 0, len));
			}

			return content.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}

}
