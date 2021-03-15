package org.elastos.hive;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
			subscription.unsubscribe()
					.whenComplete((result, ex) -> {
						if (ex != null) {
							fail();
							ex.printStackTrace();
						} else {
							// Checking result value;
							assertNotNull(result);
						}
					}).get();
		} catch (InterruptedException|ExecutionException e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Test
	public void testUnsubscribe() {
		try {
			subscription.unsubscribe()
					.whenComplete((result, ex) -> {
						if (ex != null) {
							fail();
							ex.printStackTrace();
						} else {
							// Checking result value;
							assertNotNull(result);
						}
					}).get();
		} catch (InterruptedException|ExecutionException e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Test
	public void testActivate() {
		try {
			subscription.activate()
					.whenComplete((result, ex) -> {
						if (ex != null) {
							fail();
							ex.printStackTrace();
						} else {
							// Checking result value;
							assertNotNull(result);
						}
					}).get();
		} catch (InterruptedException|ExecutionException e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Test
	public void testDeactivate() {
		try {
			subscription.deactivate()
					.whenComplete((result, ex) -> {
						if (ex != null) {
							fail();
							ex.printStackTrace();
						} else {
							// Checking result value;
							assertNotNull(result);
						}
					}).get();
		} catch (InterruptedException|ExecutionException e1) {
			e1.printStackTrace();
			fail();
		}
	}


	@BeforeClass
	public static void setup() {
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
}
