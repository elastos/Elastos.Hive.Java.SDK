package org.elastos.hive;

import org.elastos.did.PresentationInJWT;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class UserFactory {
	private static final String didCachePath = "didCache";
	private Vault vault;
	private static boolean resolverDidSetup = false;

	static class Options {
		private String storePath;
		private String ownerDid;
		private String resolveUrl;
		private String provider;

		public static Options create() {
			return new Options();
		}

		public Options storePath(String storePath) {
			this.storePath = storePath;
			return this;
		}

		public Options ownerDid(String ownerDid) {
			this.ownerDid = ownerDid;
			return this;
		}

		public Options resolveUrl(String resolveUrl) {
			this.resolveUrl = resolveUrl;
			return this;
		}

		public Options provider(String provider) {
			this.provider = provider;
			return this;
		}
	}

	private void setUp(PresentationInJWT.Options userDidOpt, PresentationInJWT.Options appInstanceDidOpt, Options userFactoryOpt) {
		try {
			PresentationInJWT presentationInJWT = new PresentationInJWT().init(userDidOpt, appInstanceDidOpt);
			if (!resolverDidSetup) {
				Client.setupResolver(userFactoryOpt.resolveUrl, this.didCachePath);
				resolverDidSetup = true;
			}
			Client.Options options = new Client.Options();
			options.setLocalDataPath(userFactoryOpt.storePath);
			options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(()
					-> presentationInJWT.getAuthToken(jwtToken)));
			options.setAuthenticationDIDDocument(presentationInJWT.getDoc());

			Client client = Client.createInstance(options);
			client.createVault(userFactoryOpt.ownerDid, userFactoryOpt.provider).whenComplete((ret, throwable) -> {
				if (throwable == null) {
					vault = ret;
				} else {
					try {
						vault = client.getVault(userFactoryOpt.ownerDid, userFactoryOpt.provider).get();
					} catch (Exception e) {
						throw new CompletionException(e);
					}
				}
			}).get();
		} catch (Exception e) {
			System.out.println("Vault has been create");
		}
	}

	private UserFactory(PresentationInJWT.Options userDidOpt, PresentationInJWT.Options appInstanceDidOpt, Options userFactoryOpt) {
		setUp(userDidOpt, appInstanceDidOpt, userFactoryOpt);
	}

	public static UserFactory createFactory(PresentationInJWT.Options userDidOpt, PresentationInJWT.Options appInstanceDidOpt, Options userFactoryOpt) {
		return new UserFactory(userDidOpt, appInstanceDidOpt, userFactoryOpt);
	}

	private static UserFactory initOptions(Config config) {
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

		Options options = Options.create();
		options.provider(config.getProvider());
		options.resolveUrl(config.getResolverUrl());
		options.ownerDid(config.getUserDid());
		options.storePath(config.getStorePath());

		return new UserFactory(userDidOpt, appInstanceDidOpt, options);
	}

	//develope 环境
	public static UserFactory createUser1() {
		return initOptions(ConfigHelper.getConfigInfo("user1.conf"));
	}

	//release环境（MainNet + https://hive1.trinity-tech.io）
	public static UserFactory createUser2() {
		return initOptions(ConfigHelper.getConfigInfo("user2.conf"));
	}

	//node 环境
	public static UserFactory createUser3() {
		return initOptions(ConfigHelper.getConfigInfo("user3.conf"));
	}

	//local 环境
	public static UserFactory createUser4() {
		return initOptions(ConfigHelper.getConfigInfo("user4.conf"));
	}

	public Vault getVault() {
		return vault;
	}
}