package org.elastos.hive;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.did.DID;
import org.elastos.did.DIDBackend;
import org.elastos.did.DIDDocument;
import org.elastos.did.DefaultDIDAdapter;
import org.elastos.did.exception.DIDResolveException;
import org.elastos.did.exception.MalformedDIDException;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.ProviderNotSetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.elastos.hive.exception.BadContextProviderException;
import org.elastos.hive.exception.DIDNotPublishedException;
import org.elastos.hive.exception.NetworkException;
import org.elastos.hive.exception.DIDResolverNotSetupException;
import org.elastos.hive.exception.DIDResolverSetupException;
import org.elastos.hive.exception.DIDResoverAlreadySetupException;

/**
 * The application context would contain the resources list below:
 *  - the reference of application context provider;
 *  - the user did which uses this application.
 */
public class AppContext {
	private static final Logger log = LoggerFactory.getLogger(AppContext.class);
	private static boolean resolverHasSetup = false;

	private AppContextProvider contextProvider;
	private String userDid;

	private AppContext(AppContextProvider provider, String userDid) {
		this.userDid = userDid;
		this.contextProvider = provider;
	}

	/**
	 * Get the provider of the application context.
	 *
	 * @return The provider of the application context.
	 */
	public AppContextProvider getAppContextProvider() {
		return this.contextProvider;
	}

	/**
	 * Get the user DID.
	 *
	 * @return The user DID.
	 */
	public String getUserDid() {
		return userDid;
	}

	/**
	 * Setup the resolver for the DID verification.
	 *
	 * @param resolver The URL of the resolver.
	 * @param cacheDir The local directory for DID cache.
	 * @throws HiveException See {@link HiveException}
	 */
	public static void setupResolver(String resolver, String cacheDir) throws HiveException {
		if (cacheDir == null || resolver == null)
			throw new IllegalArgumentException("Invalid parameters to setup DID resolver");

		if (resolverHasSetup)
			throw new DIDResoverAlreadySetupException();

		DIDBackend.initialize(new DefaultDIDAdapter(resolver));
		resolverHasSetup = true;
	}

	/**
	 * Create the application context.
	 *
	 * @param provider The provider of the application context.
	 * @param userDid The user DID.
	 * @return The application context.
	 */
	public static AppContext build(AppContextProvider provider, String userDid) {
		if (provider == null)
			throw new IllegalArgumentException("Missing AppContext provider");

		if (provider.getLocalDataDir() == null)
			throw new BadContextProviderException("Missing method to acquire data location");

		if (provider.getAppInstanceDocument() == null)
			throw new BadContextProviderException("Missing method to acquire App instance DID document");

		if (!resolverHasSetup)
			throw new DIDResolverNotSetupException();

		return new AppContext(provider, userDid);
	}

	/**
	 * Get the URL address of the provider by the user DID.
	 * The will access the property of the user DID.
	 *
	 * @param targetDid The user DID.
	 * @return The URL address of the provider
	 */
	public static CompletableFuture<String> getProviderAddress(String targetDid) {
		return getProviderAddress(targetDid, null);
	}

	/**
	 * Get the URL address of the provider by the user DID.
	 * The will access the property of the user DID.
	 *
	 * @param targetDid The user DID.
	 * @param preferredProviderAddress The preferred URL address of the provider.
	 * @return The URL address of the provider
	 */
	public static CompletableFuture<String> getProviderAddress(String targetDid, String preferredProviderAddress) {
		return CompletableFuture.supplyAsync(() -> {
			if (targetDid == null)
				throw new IllegalArgumentException("Missing input parameter for target Did");

			// Prioritize the use of external input value for 'preferredProviderAddress';
			if (preferredProviderAddress != null)
				return preferredProviderAddress;

			try {
				List<DIDDocument.Service> services = null;
				DID did = new DID(targetDid);
				DIDDocument doc;

				doc = did.resolve();
				if (doc == null)
					throw new DIDNotPublishedException(
							String.format("The DID %s has not published onto sideChain", targetDid));

				services = doc.selectServices((String) null, "HiveVault");
				if (services == null || services.size() == 0)
					throw new ProviderNotSetException(
							String.format("No 'HiveVault' services declared on DID document %s", targetDid));

				/*
				 * Should we throw special exception when it has more than one end-point
				 * of service "HiveVault";
				 */
				return services.get(0).getServiceEndpoint();

			} catch (MalformedDIDException e) {
				log.error("Malformed target did {} with error: {}", targetDid, e.getMessage());
				throw new IllegalArgumentException("Malformed did string: " + targetDid);

			} catch (DIDResolveException e) {
				log.error("Resolving the target DID {} failed: {}", targetDid, e.getMessage());
				throw new CompletionException(new NetworkException("Resolving DID failed: " + e.getMessage()));
			}
		});
	}
}
