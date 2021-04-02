package org.elastos.hive;

import org.elastos.hive.config.TestData;
import org.junit.jupiter.api.*;

@Disabled
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VaultSubscriptionTest {
	private static VaultSubscription subscription;

	@BeforeAll public static void setup() {
		Assertions.assertDoesNotThrow(()->{
			TestData testData = TestData.getInstance();
			subscription = new VaultSubscription(
					testData.getAppContext(),
					testData.getOwnerDid(),
					testData.getProviderAddress());
		});
	}

	@Test @Order(1) void testSubscribe() {
		Assertions.assertDoesNotThrow(()->subscription.subscribe("free").get());
	}

	@Test @Order(2) void testActivate() {
		Assertions.assertDoesNotThrow(()->subscription.activate().get());
	}

	@Test @Order(3) void testCheckSubscription() {
		Assertions.assertDoesNotThrow(()->Assertions.assertNotNull(subscription.checkSubscription()));
	}

	@Test @Order(4) void testDeactivate() {
		Assertions.assertDoesNotThrow(()->subscription.deactivate().get());
	}

	@Test @Order(5) void testUnsubscribe() {
		Assertions.assertDoesNotThrow(()->subscription.unsubscribe().get());
	}
}
