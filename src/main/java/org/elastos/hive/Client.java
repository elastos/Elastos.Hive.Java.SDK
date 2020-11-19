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
import org.elastos.did.DIDBackend;
import org.elastos.did.DIDDocument;
import org.elastos.did.backend.ResolverCache;
import org.elastos.did.exception.DIDException;
import org.elastos.did.exception.DIDResolveException;
import org.elastos.hive.exception.CreateVaultException;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.ProviderNotSetException;
import org.elastos.hive.exception.VaultNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class Client {
	private static boolean resolverDidSetup;

	private AuthenticationHandler authentcationHandler;
	private DIDDocument authenticationDIDDocument;
	private String localDataPath;
	private Map<String, String> cachedProviders;

	private Client(Options options) {
		this.authenticationDIDDocument = options.authenticationDIDDocument();
		this.authentcationHandler = options.authentcationHandler;
		this.localDataPath = options.localDataPath;

		this.cachedProviders = new HashMap<>();
	}

	/**
	 * Constructor without parameters
	 * resolver url and cache path use default value,
	 * resolver url default value: http://api.elastos.io:20606
	 * cache path default value: new java.io.File("didCache")
	 *
	 * @throws HiveException
	 */
	public static void setupResolver() throws HiveException {
		setupResolver(null, null);
	}

	/**
	 * Recommendation for cache dir:
	 * - Laptop/standard Java
	 * System.getProperty("user.home") + "/.cache.did.elastos"
	 * - Android Java
	 * Context.getFilesDir() + "/.cache.did.elastos"
	 *
	 * @param resolver the DIDResolver object
	 * @param cacheDir the cache path name
	 */
	public static void setupResolver(String resolver, String cacheDir) throws HiveException {
		if (cacheDir == null || resolver == null)
			throw new IllegalArgumentException();
		if (resolverDidSetup)
			throw new HiveException("Resolver already setuped");
		try {
			DIDBackend.initialize(resolver, cacheDir);
			ResolverCache.reset();
			resolverDidSetup = true;
		} catch (DIDResolveException e) {
			e.printStackTrace();
			throw new HiveException(e.getMessage());
		}
	}

	/**
	 * authentication options, include:
	 * AuthenticationHandler, DIDDocument, data cache path
	 */
	public static class Options {
		private AuthenticationHandler authentcationHandler;
		private DIDDocument authenticationDIDDocument;
		private String localDataPath;

		public Options setAuthenticationDIDDocument(DIDDocument document) {
			this.authenticationDIDDocument = document;
			return this;
		}

		protected DIDDocument authenticationDIDDocument() {
			return authenticationDIDDocument;
		}

		public Options setAuthenticationHandler(AuthenticationHandler authentcationHandler) {
			this.authentcationHandler = authentcationHandler;
			return this;
		}

		protected AuthenticationHandler authenticationHandler() {
			return authentcationHandler;
		}

		public Options setLocalDataPath(String path) {
			this.localDataPath = path;
			return this;
		}

		protected String localDataPath() {
			return localDataPath;
		}

		protected boolean checkValid() {
			return (authenticationDIDDocument != null
					&& authentcationHandler != null
					&& localDataPath != null);
		}
	}

	/**
	 * get Client instance
	 *
	 * @param options authentication options
	 * @return
	 * @throws HiveException
	 * @see Options
	 */
	public static Client createInstance(Options options) throws HiveException {
		if (options == null || !options.checkValid())
			throw new IllegalArgumentException();

		if (!resolverDidSetup)
			throw new HiveException("Setup did resolver first");

		return new Client(options);
	}

	/**
	 * get Vault
	 *
	 * @param ownerDid vault owner did
	 * @return
	 */
	public CompletableFuture<Vault> getVault(String ownerDid) {
		if (ownerDid == null)
			throw new IllegalArgumentException("Empty ownerDid");

		return getVaultProvider(ownerDid)
				.thenApply(provider -> newVault(provider, ownerDid))
				.thenApply(vault -> {
					try {
						if(null == vault.getUsingPricePlan())
							throw new VaultNotFoundException();
					} catch (Exception e) {
						e.printStackTrace();
						throw new CompletionException(e);
					}
					return vault;
				});
	}

	private Vault newVault(String provider, String ownerDid) {
		if (provider == null)
			throw new ProviderNotSetException(ProviderNotSetException.EXCEPTION);
		AuthHelper authHelper = new AuthHelper(ownerDid, provider,
				localDataPath,
				authenticationDIDDocument,
				authentcationHandler);
		return new Vault(authHelper, provider, ownerDid);
	}

	/**
	 * create Vault
	 *
	 * @param ownerDid
	 * @return
	 */
	public CompletableFuture<Vault> createVault(String ownerDid) {
		if (ownerDid == null)
			throw new IllegalArgumentException("Empty ownerDid");

		return getVaultProvider(ownerDid)
				.thenApply(provider -> newVault(provider, ownerDid))
				.thenApply(vault -> {
					try {
						vault.useTrial();
					} catch (Exception e) {
						throw new CreateVaultException();
					}
					return vault;
				});
	}

	/**
	 * Tries to find a vault address in the public DID document of the given
	 * user's DID.
	 * <p>
	 * This API always tries to fetch this information from ID chain first
	 * (vault address published publicly for this user) and falls back to the
	 * local DID/Vault mapping if it fails to resolve from chain.
	 * <p>
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
					services = doc.selectServices((String) null, "HiveVault");

				if (services != null && services.size() > 0) {
					vaultProvider = services.get(0).getServiceEndpoint();
					cachedProviders.put(ownerDid, vaultProvider);
				} else
					vaultProvider = cachedProviders.get(ownerDid);
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
	 * @param ownerDid     the DID for the vault owner
	 * @param vaultAddress the given vault address
	 */
	public void setVaultProvider(String ownerDid, String vaultAddress) {
		if (ownerDid == null || vaultAddress == null)
			throw new IllegalArgumentException();

		cachedProviders.put(ownerDid, vaultAddress);
	}
}
