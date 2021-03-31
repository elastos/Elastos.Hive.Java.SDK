package org.elastos.hive;

import com.google.common.base.Throwables;
import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;

import org.elastos.hive.exception.HiveException;
import org.junit.jupiter.api.*;

@Disabled
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VaultSubscriptionTest {
	private static VaultSubscription subscription;

	@BeforeAll
	public static void setup() {
		try {
			TestData testData = TestData.getInstance();
			subscription = new VaultSubscription(testData.getAppContext(), testData.getOwnerDid(), testData.getProviderAddress());
		} catch (HiveException | DIDException e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(1)
	void testSubscribe() {
		try {
			subscription.subscribe("free").get();
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(2)
	void testActivate() {
		try {
			subscription.activate().get();
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(3)
	void testGetPricingPlanList() {
		try {
			subscription.getPricingPlanList().get();
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(4)
	void testGetPricingPlan() {
		try {
			subscription.getPricingPlan("free").get();
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(5)
	void testPlaceOrder() {
		try {
			subscription.placeOrder("").get();
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(6)
	void testGetOrder() {

	}

	@Test
	@Order(7)
	void testPayOrder() {

	}

	@Test
	@Order(8)
	void testGetReceipt() {

	}

	@Test
	@Order(9)
	void testDeactivate() {
		try {
			subscription.deactivate().get();
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(10)
	void testUnsubscribe() {
		try {
			subscription.unsubscribe().get();
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}
}
