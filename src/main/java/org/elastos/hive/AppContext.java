package org.elastos.hive;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.elastos.did.DID;
import org.elastos.did.DIDBackend;
import org.elastos.did.DIDDocument;
import org.elastos.did.backend.ResolverCache;
import org.elastos.did.exception.DIDResolveException;
import org.elastos.did.exception.MalformedDIDException;
import org.elastos.hive.auth.LocalResolver;
import org.elastos.hive.auth.RemoteResolver;
import org.elastos.hive.auth.TokenResolver;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.ProviderNotFoundException;
import org.elastos.hive.exception.ProviderNotSetException;

/**
 * The application context would contain the resources list below:
 *  - the reference of application context provider;
 *  -
 *
 */
public class AppContext {
	private static boolean resolverHasSetup = false;

	@SuppressWarnings("unused")
	private AppContextProvider contextProvider;
	@SuppressWarnings("unused")
	private String userDid;
	@SuppressWarnings("unused")
	private String providerAddress;

	private AuthToken token;
	private TokenResolver tokenResolver;
	private ConnectionManager connectionManager;

	private AppContext(AppContextProvider provider, String userDid) {
		this(provider, userDid, null);
	}

	private AppContext(AppContextProvider provider, String userDid, String providerAddress) {
		this.contextProvider = provider;
		this.connectionManager = new ConnectionManager(providerAddress, null);
		this.tokenResolver = new LocalResolver(providerAddress);
		this.tokenResolver.setNextResolver(new RemoteResolver(this, connectionManager));
	}

	/**
	 * Global check token before every service request.
	 * @throws HiveException HiveException
	 */
	public void checkToken() throws HiveException {
		if (token == null || token.isExpired()) {
			token = tokenResolver.getToken();
			this.connectionManager.refreshToken(token);
		}
	}

	public ConnectionManager getConnectionManager() {
		return this.connectionManager;
	}

	public static void setupResolver(String resolver, String cacheDir) throws HiveException {
		if (cacheDir == null || resolver == null)
			throw new IllegalArgumentException("invalid value for parameter resolver or cacheDir");

		if (resolverHasSetup)
			throw new HiveException("Resolver already setup before");

		try {
			DIDBackend.initialize(resolver, cacheDir);
			ResolverCache.reset();
			resolverHasSetup = true;
		} catch (DIDResolveException e) {
			throw new HiveException(e.getLocalizedMessage());
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

	public static AppContext build(AppContextProvider provider) {
		if (provider == null)
			throw new IllegalArgumentException("Missing AppContext provider");

		if (provider.getLocalDataDir() == null)
			throw new IllegalArgumentException("Missing method to acquire data location in AppContext provider");

		if (provider.getAppInstanceDocument() == null)
			throw new IllegalArgumentException("Missing method to acquire App instance DID document in AppContext provider");

		// if (!resolverHasSetup)
		// throw new HiveException("Setup DID resolver first");

		return new AppContext(provider, null, null);
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
				throw new IllegalArgumentException("Invalid format for DID " + targetDid);

			} catch (DIDResolveException e) {
				// throw new CompletionException(new HiveException(e.getLocalizedMessage()));
				// TODO:
				return null;
			}
		});
	}

	AuthToken getAuthToken() throws HiveException {
		return tokenResolver.getToken();
	}
}
