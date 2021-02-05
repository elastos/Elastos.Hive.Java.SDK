package org.elastos.hive;

import org.elastos.did.DIDDocument;

import java.util.concurrent.CompletableFuture;

/**
 * Application of simulation upper layer
 */
public class MockApplication extends Application {

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
		env = Type.PRODUCTION;
		return super.onCreate();
	}

	@Override
	public boolean onResume() {
		startActivity(MainActivity.class);
		return super.onResume();
	}
}
