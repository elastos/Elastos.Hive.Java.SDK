package org.elastos.hive;

import org.elastos.did.PresentationInJWT;

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
			client.createVault(this.ownerDid, this.provider).whenComplete((ret, throwable) -> {
				if (throwable == null) {
					vault = ret;
				} else {
					try {
						vault = client.getVault(ownerDid, this.provider).get();
					} catch (Exception e) {
						throw new CompletionException(e);
					}
				}
			}).get();
		} catch (Exception e) {
			System.out.println("Vault has been create");
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
		Config config = ConfigHelper.getConfigInfo("user1.conf");
		PresentationInJWT.Options userDidOpt = PresentationInJWT.Options.create()
				.setName(config.getUserName())
				.setMnemonic(config.getUserMn())
				.setPhrasepass(config.getUserPhrasepass())
				.setStorepass(config.getUserStorepass());

		PresentationInJWT.Options appInstanceDidOpt = PresentationInJWT.Options.create()
				.setName(config.getAppName())
				.setMnemonic(config.getAppMn())
				.setPhrasepass(config.getAppPhrasepass())
				.setStorepass(config.getAppStorePass());
		return new UserFactory(userDidOpt, appInstanceDidOpt, config.getProvider(), config.getResolverUrl(), config.getUserDid(), config.getStorePath());
	}

	//develope 环境
	public static UserFactory createUser1() {
		Config config = ConfigHelper.getConfigInfo("user2.conf");
		PresentationInJWT.Options userDidOpt = PresentationInJWT.Options.create()
				.setName(config.getUserName())
				.setMnemonic(config.getUserMn())
				.setPhrasepass(config.getUserPhrasepass())
				.setStorepass(config.getUserStorepass());

		PresentationInJWT.Options appInstanceDidOpt = PresentationInJWT.Options.create()
				.setName(config.getAppName())
				.setMnemonic(config.getAppMn())
				.setPhrasepass(config.getAppPhrasepass())
				.setStorepass(config.getAppStorePass());
		return new UserFactory(userDidOpt, appInstanceDidOpt, config.getProvider(), config.getResolverUrl(), config.getUserDid(), config.getStorePath());
	}

	//node 环境
	public static UserFactory createUser3() {
		Config config = ConfigHelper.getConfigInfo("user3.conf");
		PresentationInJWT.Options userDidOpt = PresentationInJWT.Options.create()
				.setName(config.getUserName())
				.setMnemonic(config.getUserMn())
				.setPhrasepass(config.getUserPhrasepass())
				.setStorepass(config.getUserStorepass());

		PresentationInJWT.Options appInstanceDidOpt = PresentationInJWT.Options.create()
				.setName(config.getAppName())
				.setMnemonic(config.getAppMn())
				.setPhrasepass(config.getAppPhrasepass())
				.setStorepass(config.getAppStorePass());
		return new UserFactory(userDidOpt, appInstanceDidOpt, config.getProvider(), config.getResolverUrl(), config.getUserDid(), config.getStorePath());
	}

	//local 环境
	public static UserFactory createUser4() {
		Config config = ConfigHelper.getConfigInfo("user4.conf");
		PresentationInJWT.Options userDidOpt = PresentationInJWT.Options.create()
				.setName(config.getUserName())
				.setMnemonic(config.getUserMn())
				.setPhrasepass(config.getUserPhrasepass())
				.setStorepass(config.getUserStorepass());

		PresentationInJWT.Options appInstanceDidOpt = PresentationInJWT.Options.create()
				.setName(config.getAppName())
				.setMnemonic(config.getAppMn())
				.setPhrasepass(config.getAppPhrasepass())
				.setStorepass(config.getAppStorePass());
		return new UserFactory(userDidOpt, appInstanceDidOpt, config.getProvider(), config.getResolverUrl(), config.getUserDid(), config.getStorePath());
	}

	public Vault getVault() {
		return vault;
	}
}
