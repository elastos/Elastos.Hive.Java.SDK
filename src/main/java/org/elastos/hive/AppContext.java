package org.elastos.hive;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.elastos.did.DID;
import org.elastos.did.DIDBackend;
import org.elastos.did.DIDDocument;
import org.elastos.did.backend.ResolverCache;
import org.elastos.did.exception.DIDResolveException;
import org.elastos.did.exception.MalformedDIDException;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.ProviderNotFoundException;
import org.elastos.hive.exception.ProviderNotSetException;
import org.elastos.hive.exception.BadContextProviderException;
import org.elastos.hive.exception.IllegalDidFormatException;
import org.elastos.hive.exception.DIDResolverNotSetupException;
import org.elastos.hive.exception.DIDResolverSetupException;
import org.elastos.hive.exception.DIDResoverAlreadySetupException;

/**
 * The application context would contain the resources list below:
 *  - the reference of application context provider;
 *  -
 *
 */
public class AppContext {
	private static boolean resolverHasSetup = false;

	private AppContextProvider contextProvider;
	private String userDid;
	private String providerAddress;

	private ConnectionManager connectionManager;

	private AppContext(AppContextProvider provider, String userDid) {
		this(provider, userDid, null);
	}

	private AppContext(AppContextProvider provider, String userDid, String providerAddress) {
		this.providerAddress = providerAddress;
		this.userDid = userDid;
		this.contextProvider = provider;
		this.connectionManager = new ConnectionManager(this);
	}

	public static void setupResolver(String resolver, String cacheDir) throws HiveException {
		if (cacheDir == null || resolver == null)
			throw new IllegalArgumentException("invalid value for parameter resolver or cacheDir");

		if (resolverHasSetup)
			throw new DIDResoverAlreadySetupException();

		try {
			DIDBackend.initialize(resolver, cacheDir);
			ResolverCache.reset();
			resolverHasSetup = true;
		} catch (DIDResolveException e) {
			throw new DIDResolverSetupException(e.getMessage());
		}
	}

	public AppContextProvider getAppContextProvider() {
		return this.contextProvider;
	}

	public String getUserDid() {
		return this.userDid;
	}

	public String getProviderAddress() {
		return this.providerAddress;
	}

	public ConnectionManager getConnectionManager() {
		return this.connectionManager;
	}

	public static AppContext build(AppContextProvider provider) {
		if (provider == null)
			throw new IllegalArgumentException("Missing AppContext provider");

		if (provider.getLocalDataDir() == null)
			throw new BadContextProviderException("Missing method to acquire data location");

		if (provider.getAppInstanceDocument() == null)
			throw new BadContextProviderException("Missing method to acquire App instance DID document");

		if (!resolverHasSetup)
			throw new DIDResolverNotSetupException();

		return new AppContext(provider, null, null);
	}

	public static AppContext build(AppContextProvider provider, String userDid, String providerAddress) {
		if (provider == null)
			throw new IllegalArgumentException("Missing AppContext provider");

		if (provider.getLocalDataDir() == null)
			throw new BadContextProviderException("Missing method to acquire data location");

		if (provider.getAppInstanceDocument() == null)
			throw new BadContextProviderException("Missing method to acquire App instance DID document");

		if (!resolverHasSetup)
			throw new DIDResolverNotSetupException();

		return new AppContext(provider, userDid, providerAddress);
	}

	public static CompletableFuture<String> getProviderAddress(String targetDid) {
		return getProviderAddress(targetDid, null);
	}

	public static CompletableFuture<String> getProviderAddress(String targetDid, String preferredProviderAddress) {
		if (targetDid == null)
			throw new IllegalArgumentException("Missing input parameter for target Did");

		return CompletableFuture.supplyAsync(() -> {
			// Prioritize the use of external input value for 'preferredProviderAddress';
			if (preferredProviderAddress != null)
				return preferredProviderAddress;

			try {
				List<DIDDocument.Service> services = null;
				DID did = new DID(targetDid);
				DIDDocument doc;

				doc = did.resolve();
				if (doc == null)
					throw new ProviderNotFoundException(
							String.format("The DID %s has not published onto sideChain", targetDid));

				services = doc.selectServices((String) null, "HiveVault");
				if (services == null || services.size() == 0)
					throw new ProviderNotSetException(
							String.format("No 'HiveVault' services declared on DID document %s", targetDid));

				/*
				 * TODO: should we throw special exception when it has more than one end-point
				 * of service "HiveVault";
				 */
				return services.get(0).getServiceEndpoint();
			} catch (MalformedDIDException e) {
				throw new IllegalDidFormatException("Bad target did: " + targetDid);

			} catch (DIDResolveException e) {
				// throw new CompletionException(new HiveException(e.getLocalizedMessage()));
				// TODO:
				return null;
			}
		});
	}
}
