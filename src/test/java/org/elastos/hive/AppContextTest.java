package org.elastos.hive;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.CompletableFuture;

import org.elastos.did.DIDDocument;
import org.elastos.hive.exception.HiveException;
import org.junit.Test;

public class AppContextTest {
	@Test
	public void testRetupResover() {
		try {
			AppContext.setupResover("fake", "fake");
			assertTrue(true);
		} catch (HiveException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testBuild() {
		AppContext context = AppContext.build(new AppContextProvider() {
			@Override
			public String getLocalDataDir() {
				return "fakePath";
			}
			@Override
			public DIDDocument getAppInstanceDocument() {
				return null;
			}
			@Override
			public CompletableFuture<String> getAuthorization(String jwtToken) {
				return null;
			}
		});

		assertNotNull(context);
	}
}
