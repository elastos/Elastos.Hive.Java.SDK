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
import java.util.concurrent.CompletionException;

import org.elastos.did.DID;
import org.elastos.did.DIDBackend;
import org.elastos.did.DIDDocument;
import org.elastos.did.backend.ResolverCache;
import org.elastos.did.exception.DIDException;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.vault.AuthHelper;
import org.elastos.hive.vault.Constance;

public class Client {
	private static String resolverURL;
	private static String localPath;

	private Options opts;
	private Map<String , String> providerCache = new HashMap<>();

	public Client(Options options) {
		this.opts = options;
	}

	public static class Options {
		private AuthenticationHandler authentcationHandler;
		private DIDDocument authenticationDIDDocument;

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


		protected boolean checkValid(boolean all) {
			return (!all || authenticationDIDDocument != null)
					&& (authentcationHandler != null);
		}

	}

	public static void setResolverURL(String url) {
		resolverURL = url;
	}

	public static void setLocalPath(String path) {
		localPath = path;
	}

	public static Client createInstance(Options options) throws HiveException {
		if (options==null || localPath==null)
			throw new IllegalArgumentException();

		if (resolverURL == null)
			resolverURL = Constance.MAIN_NET_RESOLVER;

		try {
			DIDBackend.initialize(resolverURL, localPath);
			ResolverCache.reset();
		} catch (DIDException e) {
			e.printStackTrace();
			throw new HiveException(e.getMessage());
		}

		return new Client(options);
	}

	public CompletableFuture<Vault> getVault(String ownerDid) {
		if (ownerDid == null)
			throw new IllegalArgumentException("Empty ownerDid");

		return getVaultProvider(ownerDid).thenApply((provider)-> {
			AuthHelper authHelper = new AuthHelper(ownerDid, provider,
					localPath,
					opts.authenticationDIDDocument,
					opts.authentcationHandler);
			return new Vault(authHelper, provider, ownerDid);
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
		if (ownerDid == null)
			throw new IllegalArgumentException("Empty ownerDid");

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
			} catch (DIDException e) {
				e.printStackTrace();
				throw new CompletionException(new HiveException(e.getMessage()));
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
	public void setVaultProvider(String ownerDid, String vaultAddress) {
		if(null==ownerDid || vaultAddress==null) return;
		providerCache.put(ownerDid, vaultAddress);
	}
}
