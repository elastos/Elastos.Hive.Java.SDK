package org.elastos.hive;

import org.elastos.did.DIDDocument;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class TestEntry {


	private static MockApplication application;
	private static ApplicationContext applicationContext;
	@BeforeClass
	public static void setUp() {

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

		application = new MockApplication();
		application.onCreate(applicationContext);
	}

	@Test
	public void resume() {
		application.onResume(applicationContext);
	}


	@AfterClass
	public static void destroy() {
		application.onDestroy(applicationContext);
	}

}
