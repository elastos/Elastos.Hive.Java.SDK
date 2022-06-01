package org.elastos.hive;

import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.AlreadyExistsException;
import org.elastos.hive.subscription.payment.Order;
import org.elastos.hive.subscription.payment.Receipt;
import org.elastos.hive.service.PaymentService;
import org.junit.jupiter.api.*;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VaultPaymentTest {
	// TODO: to do pay, please use contract
	private static final String PRICING_PLAN_NAME = "Rookie";

	private static VaultSubscription vaultSubscription;
	private static BackupSubscription backupSubscription;
	private static PaymentService paymentService;

	@BeforeAll public static void setUp() {
		Assertions.assertDoesNotThrow(()-> vaultSubscription = TestData.getInstance().newVaultSubscription());
		try {
			vaultSubscription.subscribe();
		} catch (AlreadyExistsException e) {}
		Assertions.assertDoesNotThrow(()-> backupSubscription = TestData.getInstance().newBackupSubscription());
		try {
			backupSubscription.subscribe();
		} catch (AlreadyExistsException e) {}

		Assertions.assertDoesNotThrow(()->paymentService = TestData.getInstance().newVaultSubscription());
	}

	@Test @org.junit.jupiter.api.Order(1)
	void testGetVersion() {
		Assertions.assertDoesNotThrow(()->{
			String version = paymentService.getVersion().get();
			Assertions.assertNotNull(version);
		});
	}

	@Disabled
	@Test @org.junit.jupiter.api.Order(2)
	void testPlaceOrderVault() {
		Assertions.assertDoesNotThrow(()->{
			Order order = paymentService.placeOrder(PRICING_PLAN_NAME).get();
			Assertions.assertNotNull(order);
			Assertions.assertEquals(order.getSubscription(), "vault");
			Assertions.assertEquals(order.getPricingPlan(), PRICING_PLAN_NAME);
			Assertions.assertNotNull(order.getPayingDid());
			Assertions.assertTrue(order.getPayingAmount() > 0);
			Assertions.assertTrue(order.getCreateTime() > 0);
			Assertions.assertTrue(order.getExpirationTime() > 0);
			Assertions.assertNotNull(order.getReceivingAddress());
			Assertions.assertNotNull(order.getProof());
		});
	}

	@Disabled
	@Test @org.junit.jupiter.api.Order(2)
	void testPlaceOrderBackup() {
		Assertions.assertDoesNotThrow(()->{
			Order order = paymentService.placeOrder(PRICING_PLAN_NAME).get();
			Assertions.assertNotNull(order);
			Assertions.assertEquals(order.getSubscription(), "backup");
			Assertions.assertEquals(order.getPricingPlan(), PRICING_PLAN_NAME);
			Assertions.assertNotNull(order.getPayingDid());
			Assertions.assertTrue(order.getPayingAmount() > 0);
			Assertions.assertTrue(order.getCreateTime() > 0);
			Assertions.assertTrue(order.getExpirationTime() > 0);
			Assertions.assertNotNull(order.getReceivingAddress());
			Assertions.assertNotNull(order.getProof());
		});
	}

	@Disabled
	@Test @org.junit.jupiter.api.Order(3)
	void testSettleOrder() {
		Assertions.assertDoesNotThrow(()->{
			Receipt receipt = paymentService.settleOrder(3).get();
			Assertions.assertNotNull(receipt);
			Assertions.assertNotNull(receipt.getReceiptId());
			Assertions.assertNotNull(receipt.getOrderId());
			Assertions.assertEquals(receipt.getSubscription(), "vault");
			Assertions.assertEquals(receipt.getPricingPlan(), PRICING_PLAN_NAME);
			Assertions.assertTrue(receipt.getPaymentAmount() > 0);
			Assertions.assertNotNull(receipt.getPaidDid());
			Assertions.assertTrue(receipt.getCreateTime() > 0);
			Assertions.assertNotNull(receipt.getReceivingAddress());
			Assertions.assertNotNull(receipt.getReceiptProof());
		});
	}

	@Disabled
	@Test @org.junit.jupiter.api.Order(4)
	void testGetOrder() {
		Assertions.assertDoesNotThrow(()->{
			Order order = paymentService.getOrder(3).get();
			Assertions.assertNotNull(order);
			Assertions.assertEquals(order.getSubscription(), "vault");
			Assertions.assertEquals(order.getPricingPlan(), PRICING_PLAN_NAME);
			Assertions.assertNotNull(order.getPayingDid());
			Assertions.assertTrue(order.getPayingAmount() > 0);
			Assertions.assertTrue(order.getCreateTime() > 0);
			Assertions.assertTrue(order.getExpirationTime() > 0);
			Assertions.assertNotNull(order.getReceivingAddress());
			Assertions.assertNotNull(order.getProof());
		});
	}

	@Disabled
	@Test @org.junit.jupiter.api.Order(5)
	void testGetOrders() {
		Assertions.assertDoesNotThrow(()->{
			List<Order> orders = paymentService.getOrderList().get();
			Assertions.assertNotNull(orders);
			Assertions.assertFalse(orders.isEmpty());
			Order order = orders.get(0);
			Assertions.assertEquals(order.getSubscription(), "vault");
			Assertions.assertEquals(order.getPricingPlan(), PRICING_PLAN_NAME);
			Assertions.assertNotNull(order.getPayingDid());
			Assertions.assertTrue(order.getPayingAmount() > 0);
			Assertions.assertTrue(order.getCreateTime() > 0);
			Assertions.assertTrue(order.getExpirationTime() > 0);
			Assertions.assertNotNull(order.getReceivingAddress());
			Assertions.assertNotNull(order.getProof());
		});
	}

	@Disabled
	@Test @org.junit.jupiter.api.Order(6)
	void testGetReceipt() {
		Assertions.assertDoesNotThrow(()->{
			Receipt receipt = paymentService.getReceipt(3).get();
			Assertions.assertNotNull(receipt);
			Assertions.assertNotNull(receipt.getReceiptId());
			Assertions.assertNotNull(receipt.getOrderId());
			Assertions.assertEquals(receipt.getSubscription(), "vault");
			Assertions.assertEquals(receipt.getPricingPlan(), PRICING_PLAN_NAME);
			Assertions.assertTrue(receipt.getPaymentAmount() > 0);
			Assertions.assertNotNull(receipt.getPaidDid());
			Assertions.assertTrue(receipt.getCreateTime() > 0);
			Assertions.assertNotNull(receipt.getReceivingAddress());
			Assertions.assertNotNull(receipt.getReceiptProof());
		});
	}

	@Disabled
	@Test @org.junit.jupiter.api.Order(7)
	void testGetReceipts() {
		Assertions.assertDoesNotThrow(()->{
			List<Receipt> receipts = paymentService.getReceipts().get();
			Assertions.assertNotNull(receipts);
			Assertions.assertTrue(receipts.size() > 0);
			Receipt receipt = receipts.get(0);
			Assertions.assertNotNull(receipt.getReceiptId());
			Assertions.assertNotNull(receipt.getOrderId());
			Assertions.assertEquals(receipt.getSubscription(), "vault");
			Assertions.assertEquals(receipt.getPricingPlan(), PRICING_PLAN_NAME);
			Assertions.assertTrue(receipt.getPaymentAmount() > 0);
			Assertions.assertNotNull(receipt.getPaidDid());
			Assertions.assertTrue(receipt.getCreateTime() > 0);
			Assertions.assertNotNull(receipt.getReceivingAddress());
			Assertions.assertNotNull(receipt.getReceiptProof());
		});
	}
}
