package org.elastos.hive;

import com.google.common.base.Throwables;
import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
import org.junit.jupiter.api.*;

@Disabled
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BackupSubscriptionTest {
	private static BackupSubscription subscription;

	@BeforeAll
	public static void setup() {
		try {
			TestData testData = TestData.getInstance();
			subscription = new BackupSubscription(
					testData.getAppContext(),
					testData.getOwnerDid(),
					testData.getProviderAddress());
		} catch (HiveException | DIDException e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(1)
	void testSubscribe() {
		try {
			subscription.subscribe("fake_pricing_plan_name").get();
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
	void testDeactivate() {
		try {
			subscription.deactivate().get();
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(4)
	void testUnsubscribe() {
		try {
			subscription.unsubscribe().get();
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}
}
