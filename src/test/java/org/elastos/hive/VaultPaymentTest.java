package org.elastos.hive;

import org.elastos.hive.config.TestData;
import org.elastos.hive.subscription.VaultInfo;
import org.elastos.hive.subscription.payment.Order;
import org.elastos.hive.subscription.payment.Receipt;
import org.elastos.hive.service.PaymentService;
import org.elastos.hive.service.SubscriptionService;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VaultPaymentTest {
	private static SubscriptionService<VaultInfo> subscriptionService;
	private static PaymentService paymentService;

	@BeforeAll public static void setUp() {
		Assertions.assertDoesNotThrow(()->{
			TestData testData = TestData.getInstance();
			VaultSubscription vs = new VaultSubscription(
					testData.getAppContext(),
					testData.getProviderAddress());
			subscriptionService = vs;
			paymentService = vs;
		});
	}

	@Test @Disabled
	@org.junit.jupiter.api.Order(3)
	void testOrderProcess() {
		Assertions.assertDoesNotThrow(()->{
			Order order = paymentService.placeOrder(VaultSubscriptionTest.PRICING_PLAN_NAME).get();
			Assertions.assertNotNull(order);
			order = paymentService.getOrder(order.getOrderId()).get();
			Assertions.assertNotNull(order);
			Receipt receipt = paymentService.payOrder(order.getOrderId(), null).get();
			Assertions.assertNotNull(receipt);
		});
	}

	@Test @org.junit.jupiter.api.Order(4) void testGetReceipt() {
	}
}
