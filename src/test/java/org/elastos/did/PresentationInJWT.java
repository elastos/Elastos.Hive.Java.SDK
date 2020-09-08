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

package org.elastos.did;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.elastos.did.DID;
import org.elastos.did.DIDBackend;
import org.elastos.did.DIDDocument;
import org.elastos.did.DIDStore;
import org.elastos.did.Issuer;
import org.elastos.did.Issuer.CredentialBuilder;
import org.elastos.did.Mnemonic;
import org.elastos.did.VerifiableCredential;
import org.elastos.did.VerifiablePresentation;
import org.elastos.did.adapter.DummyAdapter;
import org.elastos.did.exception.DIDException;
import org.elastos.did.jwt.Claims;
import org.elastos.did.jwt.Header;
import org.elastos.did.jwt.Jws;
import org.elastos.did.jwt.JwsSignatureException;
import org.elastos.did.jwt.JwtParser;
import org.elastos.did.jwt.JwtParserBuilder;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.Files;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.file.FileInfo;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class PresentationInJWT {
	// DummyAdapter only for demo and testing.
	private static DummyAdapter adapter;
	private static final String localDataPath = System.getProperty("user.dir") + File.separator + "store";
	private static Files filesApi;

	private static Client client;

	public static class Entity {
		// Mnemonic passphrase and the store password should set by the end user.
		private final static String passphrase = "mypassphrase";
		protected final static String storepass = "password";

		private String name;
		private DIDStore store;
		private DID did;

		protected Entity(String name) throws DIDException {
			this.name = name;

			initPrivateIdentity();
			initDid();
		}

		protected void initPrivateIdentity() throws DIDException {
			final String storePath = System.getProperty("java.io.tmpdir")
					+ File.separator + name + ".store";

			// Create a fake adapter, just print the tx payload to console.
			store = DIDStore.open("filesystem", storePath, adapter);

			// Check the store whether contains the root private identity.
			if (store.containsPrivateIdentity())
				return; // Already exists

			// Create a mnemonic use default language(English).
			Mnemonic mg = Mnemonic.getInstance();
			String mnemonic = mg.generate();

			System.out.format("[%s] Please write down your mnemonic and passwords:%n", name);
			System.out.println("  Mnemonic: " + mnemonic);
			System.out.println("  Mnemonic passphrase: " + passphrase);
			System.out.println("  Store password: " + storepass);

			// Initialize the root identity.
			store.initPrivateIdentity(null, mnemonic, passphrase, storepass);
		}

		protected void initDid() throws DIDException {
			// Check the DID store already contains owner's DID(with private key).
			List<DID> dids = store.listDids(DIDStore.DID_HAS_PRIVATEKEY);
			if (dids.size() > 0) {
				for (DID did : dids) {
					if (did.getMetadata().getAlias().equals("me")) {
						// Already create my DID.
						System.out.format("[%s] My DID: %s%n", name, did);
						this.did = did;

						// This only for dummy backend.
						// normally don't need this on ID sidechain.
						store.publishDid(did, storepass);
						return;
					}
				}
			}

			DIDDocument doc = store.newDid("me", storepass);
			this.did = doc.getSubject();
			System.out.format("[%s] My new DID created: %s%n", name, did);
			store.publishDid(did, storepass);
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

	public static class DIDApp extends Entity {
		private Issuer issuer;

		public DIDApp(String name) throws DIDException {
			super(name);

			issuer = new Issuer(getDocument());
		}

		public VerifiableCredential issueDiplomaFor(DApp dapp) throws DIDException {
			Map<String, String> subject = new HashMap<String, String>();
			subject.put("'appDid'", dapp.appId);

			Calendar exp = Calendar.getInstance();
			exp.add(Calendar.YEAR, 5);

			CredentialBuilder cb = issuer.issueFor(dapp.getDid());
			VerifiableCredential vc = cb.id("diploma")
				.type("AppIdCredential")
				.properties(subject)
				.expirationDate(exp.getTime())
				.seal(getStorePassword());

			System.out.println("VerifiableCredential:");
			String vcStr = vc.toJson(true, true);
			System.out.println(vcStr);

			return vc;
		}
	}

	public static class DApp extends Entity {
		public String appId = "appId";

		public DApp(String name) throws DIDException {
			super(name);
		}

		public VerifiablePresentation createPresentation(VerifiableCredential vc, String realm, String nonce) throws DIDException {
			VerifiablePresentation.Builder vpb = VerifiablePresentation.createFor(getDid(), getDIDStore());
			List<VerifiableCredential> vcs = new ArrayList<VerifiableCredential>(1);
			vcs.add(vc);

			VerifiablePresentation vp = vpb.credentials(vcs.toArray(new VerifiableCredential[vcs.size()]))
				.realm(realm)
				.nonce(nonce)
				.seal(getStorePassword());

			System.out.println("VerifiableCredential:");
			String vpStr = vp.toJson(true);
			System.out.println(vpStr);

			return vp;
		}

		public String createToken(VerifiablePresentation vp, String hiveDid) throws DIDException {

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MILLISECOND, 0);
			Date iat = cal.getTime();
			Date nbf = cal.getTime();
			cal.add(Calendar.MONTH, 3);
			Date exp = cal.getTime();

			// Create JWT token with presentation.
			String token = getDocument().jwtBuilder()
					.addHeader(Header.TYPE, Header.JWT_TYPE)
					.setSubject("DIDAuthResponse")
					.setAudience(hiveDid)
					.setIssuedAt(iat)
					.setNotBefore(nbf)
					.setExpiration(exp)
					.claimWithJson("presentation", vp.toString())
					.sign(storepass)
					.compact();

			System.out.println("JWT Token:");
			System.out.println("  " + token);
			return token;
		}

	}

	static void initDIDBackend() throws DIDException {
		// Get DID resolve cache dir.
		final String cacheDir = System.getProperty("user.home") + File.separator + ".cache"
				+ File.separator + "elastos.did";

		// Dummy adapter for easy to use
		adapter = new DummyAdapter();

		// Initializa the DID backend globally.
		DIDBackend.initialize(adapter, cacheDir);
	}

	public static void main(String args[]) {
		try {
			PresentationInJWT.initDIDBackend();
			PresentationInJWT.DIDApp didapp = new PresentationInJWT.DIDApp("Didapp");
			PresentationInJWT.DApp testapp = new PresentationInJWT.DApp("Testapp");

			//SignIn
			DIDDocument doc = testapp.getDocument();
			String docStr = doc.toJson(true, true);
			System.out.println(docStr);


			Map map = new HashMap<>();
			JSONObject docJsonObject = new JSONObject(docStr);
			map.put("document", docJsonObject);

//			String json = new JSONObject(map).toString();
//			Response response = ConnectionManager.getHiveVaultApi()
//					.signIn(getJsonRequestBoy(json))
//					.execute();

			//Auth
			String nonce = "nonce";
			String realm = "realm";
			VerifiableCredential vc = didapp.issueDiplomaFor(testapp);

			VerifiablePresentation vp = testapp.createPresentation(vc, realm, nonce);
			String token = testapp.createToken(vp, realm);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testListFiles() {
		try {
			filesApi.list("src", new Callback<List<FileInfo>>() {
				@Override
				public void onError(HiveException e) {
					fail();
				}

				@Override
				public void onSuccess(List<FileInfo> result) {
					assertNotNull(result);
					System.out.println("size="+result);
				}
			}).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@BeforeClass
	public static void setUp() {
		try {

			PresentationInJWT.initDIDBackend();
			PresentationInJWT.DIDApp didapp = new PresentationInJWT.DIDApp("Didapp");
			PresentationInJWT.DApp testapp = new PresentationInJWT.DApp("Testapp");

			//SignIn
			DIDDocument doc = testapp.getDocument();
			String docStr = doc.toJson(true, true);
			System.out.println(docStr);

			Client.Options options = new Client.Options();

			String nonce = "nonce";
			String realm = "realm";
			VerifiableCredential vc = didapp.issueDiplomaFor(testapp);

			VerifiablePresentation vp = testapp.createPresentation(vc, realm, nonce);
			String token = testapp.createToken(vp, realm);

			options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(()
					-> token));
			options.setAuthenticationDIDDocument(doc);
			options.setDIDResolverUrl("http://api.elastos.io:21606");
			options.setLocalDataPath(localDataPath);

			Client.setVaultProvider("did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX", "http://localhost:5000");
			client = Client.createInstance(options);
			filesApi = client.getVault("did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX").get().getFiles();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
