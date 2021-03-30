package org.elastos.hive;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.VaultAlreadyExistException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;

import org.elastos.hive.exception.HiveException;
import org.junit.jupiter.api.*;

@Disabled
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VaultSubscriptionTest {
	private static VaultSubscription subscription;

	@BeforeAll
	public static void setup() {
		try {
			TestData testData = TestData.getInstance();
			subscription = new VaultSubscription(testData.getAppContext(), testData.getOwnerDid(), testData.getProviderAddress());
		} catch (HiveException | DIDException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	@Order(1)
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
	@Order(2)
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

	@Test
	@Order(3)
	public void testGetPricingPlanList() {
		try {
			subscription.getPricingPlanList()
					.whenComplete((result, ex) -> {
						if (ex != null) {
							ex.printStackTrace();
						}
					}).get();
		} catch (InterruptedException|ExecutionException e1) {
			fail();
			e1.printStackTrace();
		}
	}

	@Test
	@Order(4)
	public void testGetPricingPlan() {
		try {
			subscription.getPricingPlan("free")
					.whenComplete((result, ex) -> {
						if (ex != null) {
							ex.printStackTrace();
						}
					}).get();
		} catch (InterruptedException|ExecutionException e1) {
			fail();
			e1.printStackTrace();
		}
	}

	@Test
	@Order(5)
	public void testPlaceOrder() {
		try {
			subscription.placeOrder("")
					.whenComplete((result, ex) -> {
						if (ex != null) {
							ex.printStackTrace();
						}
					}).get();
		} catch (InterruptedException|ExecutionException e1) {
			fail();
			e1.printStackTrace();
		}
	}

	@Test
	@Order(6)
	public void testGetOrder() {

	}

	@Test
	@Order(7)
	public void testPayOrder() {

	}

	@Test
	@Order(8)
	public void testGetReceipt() {

	}

	@Test
	@Order(9)
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

	@Test
	@Order(10)
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
}
