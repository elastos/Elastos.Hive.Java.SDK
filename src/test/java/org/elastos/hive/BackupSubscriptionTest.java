package org.elastos.hive;

import org.elastos.hive.config.TestData;
import org.junit.jupiter.api.*;

@Disabled
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BackupSubscriptionTest {
	private static BackupSubscription subscription;

	@BeforeAll
	public static void setup() {
		Assertions.assertDoesNotThrow(()->{
			TestData testData = TestData.getInstance();
			subscription = new BackupSubscription(
					testData.getAppContext(),
					testData.getProviderAddress());
		} );
	}

	@Test
	@Order(1)
	void testSubscribe() {
		Assertions.assertDoesNotThrow(()->subscription.subscribe("fake_pricing_plan_name").get());
	}

	@Test
	@Order(2)
	void testActivate() {
		Assertions.assertDoesNotThrow(()->subscription.activate().get());
	}

	@Test
	@Order(3)
	void testDeactivate() {
	}

	@Test
	@Order(4)
	void testUnsubscribe() {
		Assertions.assertDoesNotThrow(()->subscription.unsubscribe().get());
	}
}
