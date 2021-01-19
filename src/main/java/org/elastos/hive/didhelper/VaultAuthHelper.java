package org.elastos.hive.didhelper;

import org.elastos.did.DIDDocument;
import org.elastos.did.Mnemonic;
import org.elastos.did.exception.DIDException;
import org.elastos.hive.Client;
import org.elastos.hive.ApplicationContext;
import org.elastos.hive.exception.HiveException;

import java.util.concurrent.CompletableFuture;

public final class VaultAuthHelper {

	private PresentationInJWT presentationInJWT;
	private String localDataDir;

	public static VaultAuthHelper createInstance(String userMnemonic, String appMnemonic, String localDataDir) {
		return new VaultAuthHelper(userMnemonic, appMnemonic, localDataDir);
	}

	public VaultAuthHelper(String userMnemonic, String appMnemonic, String localDataDir) {
		PresentationInJWT.AppOptions userDidOpt = PresentationInJWT.AppOptions.create()
				.setMnemonic(userMnemonic)
				.setStorepass("storepass");

		PresentationInJWT.AppOptions appInstanceDidOpt = PresentationInJWT.AppOptions.create()
				.setMnemonic(appMnemonic)
				.setStorepass("storepass");

		PresentationInJWT.BackupOptions backupOptions = PresentationInJWT.BackupOptions.create()
				.targetHost("https://hive1.trinity-tech.io")
				.targetDID("did:elastos:ijYUBb36yCXU6yzhydnkCCAXh7ZRW4X85J");

		presentationInJWT = new PresentationInJWT().init(userDidOpt, appInstanceDidOpt, backupOptions);
		this.localDataDir = localDataDir;
	}

	public static String generateMnemonic(String language) throws DIDException {
		Mnemonic mg = Mnemonic.getInstance(language);
		return mg.generate();
	}

	public CompletableFuture<Client> getClientWithAuth() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return Client.createInstance(new ApplicationContext() {
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
