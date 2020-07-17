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

package org.elastos.hive.vault;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public final class TestData {
	private static DummyAdapter dummyAdapter;
	private static DIDAdapter spvAdapter;

	private DIDAdapter adapter;

	private DIDDocument testIssuer;

	private DIDDocument didDocument;
	private DIDStore store;

	protected static File getResolverCacheDir() {
		return new File(System.getProperty("user.home") +
				File.separator + ".cache.did.elastos");
	}

	public DIDStore setup(boolean dummyBackend) throws DIDException {
		if (dummyBackend) {
			if (TestData.dummyAdapter == null)
				TestData.dummyAdapter = new DummyAdapter(TestConfig.verbose);
			else
				TestData.dummyAdapter.reset();

			adapter = TestData.dummyAdapter;

			DIDBackend.initialize((DIDResolver)adapter, getResolverCacheDir());
		} else {
			if (TestData.spvAdapter == null)
				TestData.spvAdapter = new SPVAdapter(TestConfig.walletDir,
						TestConfig.walletId, TestConfig.networkConfig,
						new SPVAdapter.PasswordCallback() {
							@Override
							public String getPassword(String walletDir, String walletId) {
								return TestConfig.walletPassword;
							}
						});

			adapter = TestData.spvAdapter;

			DIDBackend.initialize(TestConfig.resolver, getResolverCacheDir());
		}

		ResolverCache.reset();
    	Utils.deleteFile(new File(TestConfig.storeRoot));
    	store = DIDStore.open("filesystem", TestConfig.storeRoot, adapter);
    	return store;
	}


	public String initIdentity() throws DIDException {
    	String mnemonic =  Mnemonic.getInstance().generate();
    	store.initPrivateIdentity(Mnemonic.ENGLISH, mnemonic,
    			TestConfig.passphrase, TestConfig.storePass, true);

    	return mnemonic;
	}

	private DIDDocument loadDIDDocument(String fileName)
			throws DIDException, IOException {
		Reader input = new InputStreamReader(getClass()
				.getClassLoader().getResourceAsStream("testdata/" + fileName));
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

		store.storePrivateKey(id.getDid(), id, sk, TestConfig.storePass);
	}

	public DIDDocument loadTestIssuer() throws DIDException, IOException {
		if (testIssuer == null) {
			testIssuer = loadDIDDocument("issuer.json");

			importPrivateKey(testIssuer.getDefaultPublicKey(), "issuer.primary.sk");

			store.publishDid(testIssuer.getSubject(), TestConfig.storePass);
		}

		return testIssuer;
	}

	public DIDDocument loadTestDocument() throws DIDException, IOException {
		loadTestIssuer();

		if (didDocument == null) {
			didDocument = loadDIDDocument("document.json");

			importPrivateKey(didDocument.getDefaultPublicKey(), "document.primary.sk");
			importPrivateKey(didDocument.getPublicKey("key2").getId(), "document.key2.sk");
			importPrivateKey(didDocument.getPublicKey("key3").getId(), "document.key3.sk");

			store.publishDid(didDocument.getSubject(), TestConfig.storePass);
		}

		return didDocument;
	}

	private String loadText(String fileName) throws IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(
				getClass().getClassLoader().getResourceAsStream("testdata/" + fileName)));
		String text = input.readLine();
		input.close();

		return text;
	}

}
