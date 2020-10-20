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

package org.elastos.hive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.elastos.did.DID;
import org.elastos.did.DIDBackend;
import org.elastos.did.DIDDocument;
import org.elastos.did.backend.ResolverCache;
import org.elastos.did.exception.DIDException;
import org.elastos.hive.vault.AuthHelper;
import org.elastos.hive.vault.Constance;

public class Client {

	private Options opts;
	private static Map<String , String> providerCache = new HashMap<>();

	public Client(Options options) {
		this.opts = options;
	}

	public static class Options {
		private boolean enableCloudSync;

		private AuthenticationHandler authentcationHandler;
		private DIDDocument authenticationDIDDocument;

		public String didResolverUrl() {
			return DIDResolverUrl;
		}

		public void setDIDResolverUrl(String DIDResolverUrl) {
			this.DIDResolverUrl = DIDResolverUrl;
		}

		private String DIDResolverUrl;
		private String localPath;

		public Options setAuthenticationDIDDocument(DIDDocument document) {
			this.authenticationDIDDocument = document;
			return this;
		}

		public DIDDocument authenticationDIDDocument() {
			return this.authenticationDIDDocument;
		}

		public Options setAuthenticationHandler(AuthenticationHandler authentcationHandler) {
			this.authentcationHandler = authentcationHandler;
			return this;
		}

		public AuthenticationHandler authenticationHandler() {
			return this.authentcationHandler;
		}

		public Options setLocalDataPath(String path) {
			this.localPath = path;
			return this;
		}

		public String localDataPath() {
			return this.localPath;
		}

		protected boolean checkValid(boolean all) {
			return (localPath != null) && (!all || authenticationDIDDocument != null)
					&& (authentcationHandler != null);
		}

	}

	public static Client createInstance(Options options) {
		String resolver;

		if (options == null)
			throw new IllegalArgumentException();

		if (options.DIDResolverUrl == null)
			resolver = Constance.MAIN_NET_RESOLVER;
		else
			resolver = options.DIDResolverUrl;

		try {
			DIDBackend.initialize(resolver, options.localPath);
			ResolverCache.reset();
		} catch (DIDException e) {
			e.printStackTrace(); // TODO:
		}

		return new Client(options);
	}

	public CompletableFuture<Vault> getVault(String ownerDid) {
		if (ownerDid == null)
			throw new IllegalArgumentException("Empty ownerDid");

		return getVaultProvider(ownerDid).thenApply((vaultProvider)-> {
			Vault vault = null;
			if(vaultProvider != null) {
				AuthHelper authHelper = new AuthHelper(ownerDid, vaultProvider, opts.localPath, opts.authenticationDIDDocument, opts.authentcationHandler);
				vault = new Vault(authHelper, vaultProvider, ownerDid);
			}

			return vault;
		});
	}

	/**
	 * Tries to find a vault address in the public DID document of the given
	 * user's DID.
	 *
	 * This API always tries to fetch this information from ID chain first
	 * (vault address published publicly for this user) and falls back to the
	 * local DID/Vault mapping if it fails to resolve from chain.
	 *
	 * After being able to resolve from chain, any previously set local mapping
	 * is deleted.
	 *
	 * @param ownerDid the owner did for the vault
	 * @return the vault address in String
	 */
	public CompletableFuture<String> getVaultProvider(String ownerDid) {
		return CompletableFuture.supplyAsync(() -> {
			String vaultProvider = null;
			try {
				List<DIDDocument.Service> services = null;
				DID did = new DID(ownerDid);
				DIDDocument doc;

				doc = did.resolve();
				if (doc != null)
					services = doc.selectServices((String)null, "HiveVault");

				if (services != null && services.size() > 0) {
					vaultProvider = services.get(0).getServiceEndpoint();
					providerCache.put(ownerDid, vaultProvider);
				} else
					vaultProvider = providerCache.get(ownerDid);
			} catch (Exception e) {
				e.printStackTrace(); // TODO:
			}
			return vaultProvider;
		});
	}


	/**
	 * Locally maps the given owner DID with the given vault address. This is
	 * useful for example in case a user doesn't publish his vault address on
	 * the ID chain, and shared it privately.
	 *
	 * @param ownerDid the DID for the vault owner
	 * @param vaultAddress the given vault address
	 */
	public static void setVaultProvider(String ownerDid, String vaultAddress) {
		if(null==ownerDid || vaultAddress==null) return;
		providerCache.put(ownerDid, vaultAddress);
	}
}
