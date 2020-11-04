package org.elastos.hive;

import org.elastos.did.PresentationInJWT;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class UserFactory {

	private static final String localDataPath = System.getProperty("user.dir") + File.separator + "store";
	private static final String didCachePath = "didCache";
	private Vault vault;

	public void setUp(PresentationInJWT.Options didappOpt, PresentationInJWT.Options dappOpt) {
		try {
			PresentationInJWT presentationInJWT = new PresentationInJWT().init(didappOpt, dappOpt);
			Client.setupResolver(TestData.RESOLVER_URL, didCachePath);
			Client.Options options = new Client.Options();
			options.setLocalDataPath(localDataPath);
			options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(()
					-> presentationInJWT.getAuthToken(jwtToken)));
			options.setAuthenticationDIDDocument(presentationInJWT.getDoc());

			Client client = Client.createInstance(options);
			client.setVaultProvider(TestData.OWNERDID, TestData.PROVIDER);

			vault = client.getVault(TestData.OWNERDID).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static UserFactory mInstance;

	private UserFactory(PresentationInJWT.Options didappOpt, PresentationInJWT.Options dappOpt) {
		setUp(didappOpt, dappOpt);
	}

	public static UserFactory createFactory(PresentationInJWT.Options didappOpt, PresentationInJWT.Options dappOpt) {
		if (null == mInstance) {
			mInstance = new UserFactory(didappOpt, dappOpt);
		}
		return mInstance;
	}

	public static UserFactory createUser1() {
		if (null == mInstance) {
			PresentationInJWT.Options didappOpt = PresentationInJWT.Options.create()
					.setName(TestData.didapp1_name)
					.setMnemonic(TestData.didapp1_mn)
					.setPhrasepass(TestData.didapp1_phrasepass)
					.setStorepass(TestData.didapp1_storepass);

			PresentationInJWT.Options dappOpt = PresentationInJWT.Options.create()
					.setName(TestData.dapp1_name)
					.setMnemonic(TestData.dapp1_mn)
					.setPhrasepass(TestData.dapp1_phrasepass)
					.setStorepass(TestData.dapp1_storepass);
			mInstance = new UserFactory(didappOpt, dappOpt);
		}
		return mInstance;
	}

	public static UserFactory createUser2() {
		if (null == mInstance) {
			PresentationInJWT.Options didappOpt = PresentationInJWT.Options.create()
					.setName(TestData.didapp2_name)
					.setMnemonic(TestData.didapp2_mn)
					.setPhrasepass(TestData.didapp2_phrasepass)
					.setStorepass(TestData.didapp2_storepass);

			PresentationInJWT.Options dappOpt = PresentationInJWT.Options.create()
					.setName(TestData.dapp2_name)
					.setMnemonic(TestData.dapp2_mn)
					.setPhrasepass(TestData.dapp2_phrasepass)
					.setStorepass(TestData.dapp2_storepass);
			mInstance = new UserFactory(didappOpt, dappOpt);
		}
		return mInstance;
	}

	public Vault getVault() {
		return vault;
	}
}
