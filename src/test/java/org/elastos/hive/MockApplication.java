package org.elastos.hive;

import org.elastos.did.DIDDocument;

import java.util.concurrent.CompletableFuture;

/**
 * Application of simulation upper layer
 */
public class MockApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
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
	}

	@Override
	public void onResume() {
		super.onResume();
		startActivity(MainActivity.class);
	}
}
