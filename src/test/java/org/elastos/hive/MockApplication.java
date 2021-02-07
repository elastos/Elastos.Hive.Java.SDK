package org.elastos.hive;

import org.elastos.did.DIDDocument;
import org.elastos.did.exception.DIDException;
import org.elastos.hive.didhelper.DApp;
import org.elastos.hive.exception.HiveException;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;

/**
 * Application of simulation upper layer
 */
public class MockApplication extends Application {

	private DApp appInstanceDidApp = null;

	@Override
	public boolean onCreate() {

		applicationContext = new ApplicationContext() {
			@Override
			public String getLocalDataDir() {
				return null;
			}

			@Override
			public DIDDocument getAppInstanceDocument() {
				return null;
			}

			@Override
			public CompletableFuture<String> getAuthorization(String jwtToken) {
				return null;
			}
		};

		try {
			//TODO set application DID
			AppType appType = AppType.MAIN_NET;
			Client.setupResolver((appType==AppType.MAIN_NET)?"http://api.elastos.io:20606":"http://api.elastos.io:21606", "data/didCache");
			AppConfig appConfig = getAppConfig(appType);
			appInstanceDidApp = new DApp(appConfig.name, appConfig.mnemonic, adapter, appConfig.phrasePass, appConfig.storepass);
		} catch (DIDException e) {
			e.printStackTrace();
		} catch (HiveException e) {
			e.printStackTrace();
		}

		return super.onCreate();
	}

	@Override
	public boolean onResume() {
		startActivity(VaultActivity.class);
		return super.onResume();
	}

	private enum AppType {
		MAIN_NET,
		TEST_NET,
	}

	private static class AppConfig {
		private String name;
		private String mnemonic;
		private String phrasePass;
		private String storepass;

		public static AppConfig create() {
			return new AppConfig();
		}

		public AppConfig setName(String name) {
			this.name = name;
			return this;
		}

		public AppConfig setMnemonic(String mnemonic) {
			this.mnemonic = mnemonic;
			return this;
		}

		public AppConfig setPhrasePass(String phrasePass) {
			this.phrasePass = phrasePass;
			return this;
		}

		public AppConfig setStorepass(String storepass) {
			this.storepass = storepass;
			return this;
		}
	}

	private AppConfig getAppConfig(AppType type) {
		String fileName;
		switch (type) {
			case MAIN_NET:
				fileName = "MainNetApp.conf";
				break;
			case TEST_NET:
				fileName = "TestNetApp.conf";
				break;
			default:
				throw new IllegalArgumentException("App type is invalid");
		}
		Properties properties = Utils.getProperties(fileName);

		return AppConfig.create()
		.setMnemonic(properties.getProperty("appMn"))
		.setName(properties.getProperty("appName"))
		.setPhrasePass(properties.getProperty("appPhrasepass"))
		.setStorepass(properties.getProperty("appStorePass"));
	}

}
