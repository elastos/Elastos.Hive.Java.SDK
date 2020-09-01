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

import org.elastos.did.DID;
import org.elastos.did.DIDDocument;
import org.elastos.did.DIDURL;
import org.elastos.did.util.LRUCache;
import org.elastos.hive.vendor.vault.VaultAuthHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Client {

	private Options opts;

	private static Map<DID, Vault> vaultCache;
	private static Map<String , String> providerCache;

	public Client(Options options) {
		this.opts = options;
		providerCache = new HashMap<>();
//		this.vaultCache = LRUCache.createInstance(16, 32);
	}

	public static class Options {

//		private String did;
//		private String clientId;
//		private String clientSecret;
//		private String redirectURL;
//		private String nodeUrl;
//		private Authenticator authenticator;

		private boolean enableCloudSync;

		private AuthenticationHandler authentcationHandler;
		private String localPath;

//		public void setDid(String did) {
//			this.did = did;
//		}
//
//		public String getDid() {return this.did;}
//
//		public void setClientId(String clientId) {
//			this.clientId = clientId;
//		}
//
//		public String clientId() {return this.clientId;}
//
//		public void setClientSecret(String clientSecret) {
//			this.clientSecret = clientSecret;
//		}
//
//		public String clientSecret() {
//			return this.clientSecret;
//		}
//
//		public void setRedirectURL(String redirectURL) {
//			this.redirectURL = redirectURL;
//		}
//
//		public String redirectURL() {return this.redirectURL;}
//
//		public void setNodeUrl(String url) {
//			this.nodeUrl = url;
//		}
//
//		public String nodeUrl() {
//			return nodeUrl;
//		}

		public void setEnableCloudSync(boolean enable) {
			this.enableCloudSync = enable;
		}

		public boolean enableCloudSync() {
			return this.enableCloudSync;
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
            return /*(storePath != null) && (!all || authenticator != null)*/true;
        }

	}

	public static Client createInstance(Options options) {

		return new Client(options);
	}

	public CompletableFuture<Vault> getVault(String ownerDid) {
		return CompletableFuture.supplyAsync(() -> {
			String vaultProvider = null;
			try {
				vaultProvider = getVaultProvider(ownerDid).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			Vault vault = null;
			if(vaultProvider != null) {
				VaultAuthHelper authHelper = new VaultAuthHelper(opts.localPath, opts.authentcationHandler);
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
	public static CompletableFuture<String> getVaultProvider(String ownerDid) {
		return CompletableFuture.supplyAsync(() -> {
			String vaultProvider = null;
			try {
				DID did = new DID(ownerDid);
				DIDDocument doc = did.resolve();
				List<DIDDocument.Service> services = doc.selectServices((DIDURL) null, "HiveVault");
				if(services!=null && services.size()>0) {
					vaultProvider = services.get(0).getServiceEndpoint();
					providerCache.put(ownerDid, vaultProvider);
				} else {
					vaultProvider = providerCache.get(ownerDid);
				}
			} catch (Exception e) {
				e.printStackTrace();
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
