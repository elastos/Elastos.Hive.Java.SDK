package org.elastos.hive;

import org.elastos.hive.config.TestData;
import org.elastos.hive.subscription.AppInfo;
import org.elastos.hive.subscription.PricingPlan;
import org.elastos.hive.subscription.VaultInfo;
import org.junit.jupiter.api.*;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VaultSubscriptionTest {
	public static final String PRICING_PLAN_NAME = "Rookie";

	private static VaultSubscription subscription;

	@BeforeAll public static void setup() {
		Assertions.assertDoesNotThrow(()->subscription = TestData.getInstance().newVaultSubscription());
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
			PricingPlan plan = subscription.getPricingPlan(PRICING_PLAN_NAME).get();
			Assertions.assertNotNull(plan);
			Assertions.assertEquals(plan.getName(), PRICING_PLAN_NAME);
		});
	}

	@Disabled
	@Test @Order(3) void testSubscribe() {
		Assertions.assertDoesNotThrow(()->subscription.subscribe().get());
	}

	@Test @Order(4) void testCheckSubscription() {
		Assertions.assertDoesNotThrow(()-> {
			VaultInfo info = subscription.checkSubscription().get();
			Assertions.assertNotNull(info);
			Assertions.assertNotNull(info.getPricingPlan());
			Assertions.assertNotNull(info.getServiceDid());
			Assertions.assertTrue(info.getStorageQuota() > 0);
			Assertions.assertTrue(info.getCreated() > 0);
			Assertions.assertTrue(info.getUpdated() > 0);
			Assertions.assertTrue(info.getAppCount() > 0);
			Assertions.assertTrue(info.getAccessCount() >= 0);
			Assertions.assertTrue(info.getAccessAmount() >= 0);
			Assertions.assertTrue(info.getAccessLastTime() >= -1);
		});
	}

	@Disabled
	@Test @Order(5) void testActivate() {
		Assertions.assertDoesNotThrow(()->subscription.activate().get());
	}

	@Disabled
	@Test @Order(6) void testDeactivate() {
		Assertions.assertDoesNotThrow(()->subscription.deactivate().get());
	}

	@Disabled
	@Test @Order(7) void testGetAppStats() {
		Assertions.assertDoesNotThrow(()-> {
			List<AppInfo> infos = subscription.getAppStats().get();
			Assertions.assertNotNull(infos);
			Assertions.assertTrue(infos.size() > 0);
			Assertions.assertNotNull(infos.get(0).getName());
			Assertions.assertNotNull(infos.get(0).getDeveloperDid());
			Assertions.assertNotNull(infos.get(0).getIconUrl());
			Assertions.assertNotNull(infos.get(0).getUserDid());
			Assertions.assertNotNull(infos.get(0).getAppDid());
			Assertions.assertTrue(infos.get(0).getUsedStorageSize() >= 0);
			Assertions.assertTrue(infos.get(0).getAccessCount() >= 0);
			Assertions.assertTrue(infos.get(0).getAccessAmount() >= 0);
			Assertions.assertTrue(infos.get(0).getAccessLastTime() >= -1);
		});
	}

	@Disabled
	@Test @Order(8) void testUnsubscribe() {
		Assertions.assertDoesNotThrow(()->subscription.unsubscribe(true).get());
	}

	@Disabled
	@Test @Order(9) void testGetFileHashProcess() {
		//prepare for access vault service
		Assertions.assertDoesNotThrow(() -> {
			subscription.subscribe().get();
		});
		//function usage
		new FilesServiceTest().testHash();
		//release for disable vault service accessing
		Assertions.assertDoesNotThrow(() -> {
			subscription.unsubscribe(true).get();
		});
	}
}
