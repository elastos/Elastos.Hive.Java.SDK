package org.elastos.hive;

import org.elastos.did.PresentationInJWT;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class TestFactory {

	private static final String localDataPath = System.getProperty("user.dir") + File.separator + "store";
	private static final String didCachePath = "didCache";
	private Vault vault;

	public void setUp() {
		try {
			PresentationInJWT presentationInJWT = new PresentationInJWT().init();
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

	private static TestFactory mInstance;

	private TestFactory() {
		setUp();
	}

	public static TestFactory createFactory() {
		if (null == mInstance) {
			mInstance = new TestFactory();
		}
		return mInstance;
	}

	public Vault getVault() {
		return vault;
	}
}
