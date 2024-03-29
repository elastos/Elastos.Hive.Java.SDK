package org.elastos.hive;

import org.elastos.hive.config.TestData;
import org.elastos.hive.subscription.BackupInfo;
import org.elastos.hive.subscription.PricingPlan;
import org.junit.jupiter.api.*;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BackupSubscriptionTest {
	private static BackupSubscription subscription;

	@BeforeAll public static void setup() {
		Assertions.assertDoesNotThrow(()->subscription = TestData.getInstance().newBackupSubscription());
	}

	@Test @Order(1) void testGetPricingPlanList() {
		Assertions.assertDoesNotThrow(()->{
			List<PricingPlan> plans = subscription.getPricingPlanList().get();
			Assertions.assertNotNull(plans);
			Assertions.assertFalse(plans.isEmpty());
		});
	}

	@Test @Order(2) void testGetPricingPlan() {
		Assertions.assertDoesNotThrow(()-> {
			PricingPlan plan = subscription.getPricingPlan(VaultSubscriptionTest.PRICING_PLAN_NAME).get();
			Assertions.assertNotNull(plan);
			Assertions.assertEquals(plan.getName(), VaultSubscriptionTest.PRICING_PLAN_NAME);
		});
	}

	@Disabled
	@Test @Order(3) void testSubscribe() {
		Assertions.assertDoesNotThrow(()->subscription.subscribe().get());
	}

	@Disabled
	@Test @Order(4) void testCheckSubscription() {
		Assertions.assertDoesNotThrow(()-> {
			BackupInfo info = subscription.checkSubscription().get();
			Assertions.assertNotNull(info);
			Assertions.assertNotNull(info.getPricingPlan());
			Assertions.assertNotNull(info.getServiceDid());
			Assertions.assertTrue(info.getStorageQuota() > 0);
			Assertions.assertTrue(info.getCreated() > 0);
			Assertions.assertTrue(info.getUpdated() > 0);
		});
	}

	@Disabled
	@Test @Order(5) void testUnsubscribe() {
		Assertions.assertDoesNotThrow(()->subscription.unsubscribe().get());
	}
}
