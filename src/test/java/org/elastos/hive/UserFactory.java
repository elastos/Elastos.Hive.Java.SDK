package org.elastos.hive;

import org.elastos.did.PresentationInJWT;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class UserFactory {
	private static final String didCachePath = "didCache";
	private Vault vault;
	private static boolean resolverDidSetup = false;


	private void setUp(PresentationInJWT.Options userDidOpt, PresentationInJWT.Options appInstanceDidOpt, String tokenCachePath) {
		try {
			PresentationInJWT presentationInJWT = new PresentationInJWT().init(userDidOpt, appInstanceDidOpt);
			if(!resolverDidSetup) {
				Client.setupResolver(TestData.RESOLVER_URL, didCachePath);
				resolverDidSetup = true;
			}
			Client.Options options = new Client.Options();
			options.setLocalDataPath(tokenCachePath);
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


	private UserFactory(PresentationInJWT.Options userDidOpt, PresentationInJWT.Options appInstanceDidOpt, String tokenCachePath) {
		setUp(userDidOpt, appInstanceDidOpt, tokenCachePath);
	}

	public static UserFactory createFactory(PresentationInJWT.Options userDidOpt, PresentationInJWT.Options appInstanceDidOpt, String tokenCachePath) {
		return new UserFactory(userDidOpt, appInstanceDidOpt, tokenCachePath);
	}

	public static UserFactory createUser1() {
		final String user1 = System.getProperty("user.dir") + File.separator + "store" + File.separator + "user1";
		PresentationInJWT.Options userDidOpt = PresentationInJWT.Options.create()
				.setName(TestData.userDid1_name)
				.setMnemonic(TestData.userDid1_mn)
				.setPhrasepass(TestData.userDid1_phrasepass)
				.setStorepass(TestData.userDid1_storepass);

		PresentationInJWT.Options appInstanceDidOpt = PresentationInJWT.Options.create()
				.setName(TestData.appInstance1_name)
				.setMnemonic(TestData.appInstance1_mn)
				.setPhrasepass(TestData.appInstance1_phrasepass)
				.setStorepass(TestData.appInstance1_storepass);
		return new UserFactory(userDidOpt, appInstanceDidOpt, user1);
	}

	public static UserFactory createUser2() {
		final String user2 = System.getProperty("user.dir") + File.separator + "store" + File.separator + "user2";
		PresentationInJWT.Options userDidOpt = PresentationInJWT.Options.create()
				.setName(TestData.userDid2_name)
				.setMnemonic(TestData.userDid2_mn)
				.setPhrasepass(TestData.userDid2_phrasepass)
				.setStorepass(TestData.userDid2_storepass);

		PresentationInJWT.Options appInstanceDidOpt = PresentationInJWT.Options.create()
				.setName(TestData.appInstance2_name)
				.setMnemonic(TestData.appInstance2_mn)
				.setPhrasepass(TestData.appInstance2_phrasepass)
				.setStorepass(TestData.appInstance2_storepass);
		return new UserFactory(userDidOpt, appInstanceDidOpt, user2);
	}

	public static UserFactory createUser3() {
		final String user3 = System.getProperty("user.dir") + File.separator + "store" + File.separator + "user3";
		PresentationInJWT.Options userDidOpt = PresentationInJWT.Options.create()
				.setName(TestData.userDid3_name)
				.setMnemonic(TestData.userDid3_mn)
				.setPhrasepass(TestData.userDid3_phrasepass)
				.setStorepass(TestData.userDid3_storepass);

		PresentationInJWT.Options appInstanceDidOpt = PresentationInJWT.Options.create()
				.setName(TestData.appInstance3_name)
				.setMnemonic(TestData.appInstance3_mn)
				.setPhrasepass(TestData.appInstance3_phrasepass)
				.setStorepass(TestData.appInstance3_storepass);
		return new UserFactory(userDidOpt, appInstanceDidOpt, user3);
	}

	public Vault getVault() {
		return vault;
	}
}
