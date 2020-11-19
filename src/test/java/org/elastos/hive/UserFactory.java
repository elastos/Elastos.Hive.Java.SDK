package org.elastos.hive;

import org.elastos.did.PresentationInJWT;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class UserFactory {
	private static final String didCachePath = "didCache";
	private Vault vault;
	private static boolean resolverDidSetup = false;

	private String storePath;
	private String ownerDid;
	private String resolveUrl;
	private String provider;

	private void setUp(PresentationInJWT.Options userDidOpt, PresentationInJWT.Options appInstanceDidOpt) {
		try {
			PresentationInJWT presentationInJWT = new PresentationInJWT().init(userDidOpt, appInstanceDidOpt);
			if (!resolverDidSetup) {
				Client.setupResolver(this.resolveUrl, this.didCachePath);
				resolverDidSetup = true;
			}
			Client.Options options = new Client.Options();
			options.setLocalDataPath(this.storePath);
			options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(()
					-> presentationInJWT.getAuthToken(jwtToken)));
			options.setAuthenticationDIDDocument(presentationInJWT.getDoc());

			Client client = Client.createInstance(options);
			client.setVaultProvider(this.ownerDid, this.provider);

			client.createVault(this.ownerDid).whenComplete((ret, throwable) -> {
				if (throwable == null) {
					vault = ret;
				} else {
					try {
						vault = client.getVault(ownerDid).get();
					} catch (Exception e) {
						throw new CompletionException(e);
					}
				}
			}).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private UserFactory(PresentationInJWT.Options userDidOpt, PresentationInJWT.Options appInstanceDidOpt, String provider, String resolveUrl, String ownerDid, String tokenCachePath) {
		this.provider = provider;
		this.resolveUrl = resolveUrl;
		this.ownerDid = ownerDid;
		this.storePath = tokenCachePath;
		setUp(userDidOpt, appInstanceDidOpt);
	}

	public static UserFactory createFactory(PresentationInJWT.Options userDidOpt, PresentationInJWT.Options appInstanceDidOpt, String provider, String resolveUrl, String ownerDid, String tokenCachePath) {
		return new UserFactory(userDidOpt, appInstanceDidOpt, provider, resolveUrl, ownerDid, tokenCachePath);
	}

	//release环境（MainNet + https://hive1.trinity-tech.io + userDid1）
	public static UserFactory createUser2() {
		String user1Path = System.getProperty("user.dir") + File.separator + "store" + File.separator + "user1";
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
		return new UserFactory(userDidOpt, appInstanceDidOpt, TestData.RELEASE_PROVIDER, TestData.MAIN_RESOLVER_URL, TestData.userDid1, user1Path);
	}

	//develope 环境
	public static UserFactory createUser1() {
		String user2Path = System.getProperty("user.dir") + File.separator + "store" + File.separator + "user2";
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
		return new UserFactory(userDidOpt, appInstanceDidOpt, TestData.DEVELOP_PROVIDER, TestData.TEST_RESOLVER_URL, TestData.userDid2, user2Path);
	}

	//local 环境
	public static UserFactory createUser3() {
		String user3Path = System.getProperty("user.dir") + File.separator + "store" + File.separator + "user3";
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
		return new UserFactory(userDidOpt, appInstanceDidOpt, TestData.LOCAL_PROVIDER, TestData.MAIN_RESOLVER_URL, TestData.userDid3, user3Path);
	}

	public Vault getVault() {
		return vault;
	}
}
