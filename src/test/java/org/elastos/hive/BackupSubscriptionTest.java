package org.elastos.hive;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.BackupAlreadyExistException;
import org.elastos.hive.exception.HiveException;
import org.junit.jupiter.api.*;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.fail;

@Disabled
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BackupSubscriptionTest {
	private static BackupSubscription subscription;

	@BeforeAll
	public static void setup() {
		try {
			TestData testData = TestData.getInstance();
			subscription = new BackupSubscription(testData.getAppContext(), testData.getOwnerDid(), testData.getProviderAddress());
		} catch (HiveException | DIDException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@Order(1)
	public void testSubscribe() {
		try {
			subscription.subscribe("fake_name")
					.whenComplete((result, ex) -> {
						if (ex != null) {
							ex.printStackTrace();
						}
					}).get();
		} catch (InterruptedException| ExecutionException e1) {
			if(BackupAlreadyExistException.class.isInstance(e1.getCause())) return;
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
	@Order(4)
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
