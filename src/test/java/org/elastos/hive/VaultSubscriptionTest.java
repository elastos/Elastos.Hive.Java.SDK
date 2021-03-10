package org.elastos.hive;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.elastos.did.DIDDocument;
import org.elastos.hive.exception.HiveException;
import org.junit.AfterClass;
import org.junit.BeforeClass;


public class VaultSubscriptionTest {
	private static VaultSubscription subscription;

	@Test
	public void testSubscribe() {
		try {
			subscription.subscribe("target-pricing-plan")
				.whenComplete((result, ex) -> {
					if (ex != null) {
						assertTrue(false);
						ex.printStackTrace();
					} else {
						// Checking result value;
						assertNotNull(result);
					}
				}).get();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			assertTrue(false);
		} catch (ExecutionException e1) {
			e1.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testUnsubscribe() {
	}

	@Test
	public void testActivate() {
	}

	@Test
	public void testDeactivate() {
	}


	@BeforeClass
	public void setup() {
		AppContext context;
		context = AppContext.build(new AppContextProvider() {
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
		});

		try {
			subscription = new VaultSubscription(context, "target-provider-address", "your-user-did");
		} catch (HiveException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@AfterClass
	public void tearDown() {}
}
