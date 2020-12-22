package org.elastos.did;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.Client;
import org.elastos.hive.HiveContext;
import org.elastos.hive.exception.HiveException;

import java.util.concurrent.CompletableFuture;

public final class VaultAuthHelper {

	private PresentationInJWT presentationInJWT;
	private String localDataDir;

	public static VaultAuthHelper createInstance(String userMn, String appMn, String localDataDir) {
		return new VaultAuthHelper(userMn, appMn, localDataDir);
	}

	public VaultAuthHelper(String userMn, String appMn, String localDataDir) {
		PresentationInJWT.Options userDidOpt = PresentationInJWT.Options.create()
				.setMnemonic(userMn)
				.setStorepass("storepass");

		PresentationInJWT.Options appInstanceDidOpt = PresentationInJWT.Options.create()
				.setMnemonic(appMn)
				.setStorepass("storepass");

		presentationInJWT = new PresentationInJWT().init(userDidOpt, appInstanceDidOpt);
		this.localDataDir = localDataDir;
	}

	public static String generateMnemonic(String language) throws DIDException {
		Mnemonic mg = Mnemonic.getInstance(language);
		return mg.generate();
	}

	public CompletableFuture<Client> getClientWithAuth() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return Client.createInstance(new HiveContext() {
					@Override
					public String getLocalDataDir() {
						return localDataDir;
					}

					@Override
					public DIDDocument getAppInstanceDocument() {
						return getAppDIDDocument();
					}

					@Override
					public CompletableFuture<String> getAuthorization(String jwtToken) {
						return generateAuthPresentationJWT(jwtToken);
					}
				});
			} catch (HiveException e) {
				e.printStackTrace();
			}
			return null;
		});
	}

	public CompletableFuture<String> generateAuthPresentationJWT(String challengeJwtToken) {
		return CompletableFuture.supplyAsync(() -> presentationInJWT.getAuthToken(challengeJwtToken));
	}

	public DIDDocument getAppDIDDocument() {
		return presentationInJWT.getDoc();
	}

}
