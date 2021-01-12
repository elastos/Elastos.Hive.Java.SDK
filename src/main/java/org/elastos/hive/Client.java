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
import org.elastos.hive.exception.BackupVaultAlreadyExistException;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.ProviderNotSetException;
import org.elastos.hive.exception.VaultAlreadyExistException;

import java.nio.file.ProviderNotFoundException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class Client {
	private static boolean resolverDidSetup;

	private AuthenticationAdapter authenticationAdapter;
	private ApplicationContext context;

	private static class AuthenticationAdapterImpl implements AuthenticationAdapter {
		@Override
		public synchronized CompletableFuture<String> getAuthorization(ApplicationContext context, String jwtToken) {
			return context.getAuthorization(jwtToken);
		}
	}

	private Client(ApplicationContext context) {
		this.context = context;
		this.authenticationAdapter = new AuthenticationAdapterImpl();
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
			throw new HiveException("Resolver already setup before");

		try {
			DIDBackend.initialize(resolver, cacheDir);
			ResolverCache.reset();
			resolverDidSetup = true;
		} catch (DIDResolveException e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}


	public static Client createInstance(ApplicationContext context) throws HiveException {
		if (context == null)
            throw new IllegalArgumentException("Missing Application context");

        if (context.getLocalDataDir() == null)
            throw new IllegalArgumentException("Can not acquire data cache location from Application context");

        if (context.getAppInstanceDocument() == null)
            throw new IllegalArgumentException("Can not acquire App instance document from Application context");

		if (!resolverDidSetup)
			throw new HiveException("Setup DID resolver first");

		return new Client(context);
	}

	/**
	 * get Vault instance with specified DID.
	 * Try to get a vault on target provider address with following steps:
	 *  - Get the target provider address;
	 *  - Create a new vaule of local instance..
	 *
	 * @param ownerDid  the owner did related to target vault
	 * @param preferredProviderAddress the preferred target provider address
	 * @return a new vault instance.
	 */
	public CompletableFuture<Vault> getVault(String ownerDid, String preferredProviderAddress) {
		return getVaultProvider(ownerDid, preferredProviderAddress)
				.thenApplyAsync(provider -> {
					AuthHelper authHelper = new AuthHelper(this.context,
							ownerDid,
							provider,
							this.authenticationAdapter);
					return new Vault(authHelper, provider, ownerDid);
				});
	}

	/**
	 * Create Vault for user with specified DID.
	 * Try to create a vault on target provider address with following steps:
	 *  - Get the target provider address;
	 *  - Check whether the vault is already existed on target provider, otherwise
	 *  - Create a new vault on target provider with free pricing plan.
	 *
	 * @param ownerDid  the owner did that want to create a vault
	 * @param preferredProviderAddress the preferred provider address to use
	 * @return a new created vault for owner did
	 */
	public CompletableFuture<Vault> createVault(String ownerDid, String preferredProviderAddress) {

		return getVaultProvider(ownerDid, preferredProviderAddress)
				.thenApplyAsync(provider -> {
					AuthHelper authHelper = new AuthHelper(this.context,
							ownerDid,
							provider,
							this.authenticationAdapter);
					return new Vault(authHelper, provider, ownerDid);
				})
				.thenComposeAsync(vault -> vault.checkVaultExist().thenApplyAsync(aBoolean -> {
					if(aBoolean) {
						throw new VaultAlreadyExistException("Vault already existed.");
					} else {
						vault.createVaultOnService();
					}
					return vault;
				}));
	}

	/**
	 * Try to acquire provider address for the specific user DID with rules with sequence orders:
	 *  - Use 'preferredProviderAddress' first when it's being with real value; Otherwise
	 *  - Resolve DID document according to the ownerDid from DID sidechain,
	 *    and find if there are more than one "HiveVault" services, then would
	 *    choose the first one service point as target provider address. Otherwise
	 *  - It means no service endpoints declared on this DID Document, then would throw the
	 *    corresponding exception.
	 *
	 * @param ownerDid the owner did that want be set provider address
	 * @param preferredProviderAddress the preferred provider address to use
	 * @return the provider address
	 */
	public CompletableFuture<String> getVaultProvider(String ownerDid, String preferredProviderAddress) {
		if (ownerDid == null)
			throw new IllegalArgumentException("Missing ownerDid to get the provider for");

		return CompletableFuture.supplyAsync(() -> {
			/* Choose 'preferredProviderAddress' as target provider address if it's with value;
			 */
			if (preferredProviderAddress != null)
				return preferredProviderAddress;

			try {
				List<DIDDocument.Service> services = null;
				DID did = new DID(ownerDid);
				DIDDocument doc;

				doc = did.resolve();
				if (doc == null)
					throw new ProviderNotFoundException(
							String.format("The DID document %s has not published", ownerDid));

				services = doc.selectServices((String) null, "HiveVault");
				if (services == null || services.size() == 0)
					throw new ProviderNotSetException(
							String.format("No 'HiveVault' services declared on DID document %s", ownerDid));

				/* TODO: should we throw special exception when it has more than one
				 *       endpoints of service "HiveVault";
				 */
				return services.get(0).getServiceEndpoint();
			} catch (DIDException e) {
				throw new CompletionException(new HiveException(e.getLocalizedMessage()));
			}
		});
	}

	public CompletableFuture<Boolean> createBackupVault(Vault vault) {
		if(null == vault) {
			throw new IllegalArgumentException("vault can not be null");
		}
		return vault.checkBackupVaultExist().thenComposeAsync(aBoolean -> {
			if(aBoolean) {
				throw new BackupVaultAlreadyExistException("Backup Vault already existed.");
			}
			return vault.createBackupVaultOnService();
		});
	}
}
