package org.elastos.hive;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.VaultAlreadyExistException;
import org.junit.Ignore;
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
			subscription.subscribe("free")
					.whenComplete((result, ex) -> {
						if (ex != null) {
							ex.printStackTrace();
						}
					}).get();
		} catch (InterruptedException|ExecutionException e1) {
			if(VaultAlreadyExistException.class.isInstance(e1.getCause())) {
				return;
			}
			fail();
			e1.printStackTrace();
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
						}
					}).get();
		} catch (InterruptedException|ExecutionException e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Ignore
	@Test
	public void testDeactivate() {
		try {
			subscription.deactivate()
					.whenComplete((result, ex) -> {
						if (ex != null) {
							fail();
							ex.printStackTrace();
						}
					}).get();
		} catch (InterruptedException|ExecutionException e1) {
			e1.printStackTrace();
			fail();
		}
	}


	@BeforeClass
	public static void setup() {
		try {
			TestData testData = TestData.getInstance();
			subscription = new VaultSubscription(testData.getAppContext(), testData.getOwnerDid(), testData.getProviderAddress());
		} catch (HiveException | DIDException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
