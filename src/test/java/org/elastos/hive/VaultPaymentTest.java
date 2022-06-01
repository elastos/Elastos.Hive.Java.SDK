package org.elastos.hive;

import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.AlreadyExistsException;
import org.elastos.hive.subscription.payment.Order;
import org.elastos.hive.subscription.payment.Receipt;
import org.junit.jupiter.api.*;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VaultPaymentTest {
	private static final String PRICING_PLAN_NAME = "Rookie";

	private static VaultSubscription vaultSubscription;
	private static BackupSubscription backupSubscription;

	@BeforeAll public static void setUp() {
		Assertions.assertDoesNotThrow(()-> vaultSubscription = TestData.getInstance().newVaultSubscription());
		try {
			vaultSubscription.subscribe();
		} catch (AlreadyExistsException e) {}
		Assertions.assertDoesNotThrow(()-> backupSubscription = TestData.getInstance().newBackupSubscription());
		try {
			backupSubscription.subscribe();
		} catch (AlreadyExistsException e) {}
	}

	@Test @org.junit.jupiter.api.Order(1)
	void testGetVersion() {
		Assertions.assertDoesNotThrow(()->{
			String version = vaultSubscription.getVersion().get();
			Assertions.assertNotNull(version);
		});
		Assertions.assertDoesNotThrow(()->{
			String version = backupSubscription.getVersion().get();
			Assertions.assertNotNull(version);
		});
	}

	@Disabled
	@Test @org.junit.jupiter.api.Order(2)
	void testPlaceOrderVault() {
		Assertions.assertDoesNotThrow(()->{
			Order order = vaultSubscription.placeOrder(PRICING_PLAN_NAME).get();
			Assertions.assertNotNull(order);
			Assertions.assertEquals(order.getSubscription(), "vault");
			Assertions.assertEquals(order.getPricingPlan(), PRICING_PLAN_NAME);
			Assertions.assertNotNull(order.getPayingDid());
			Assertions.assertTrue(order.getPaymentAmount() > 0);
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
			Order order = backupSubscription.placeOrder(PRICING_PLAN_NAME).get();
			Assertions.assertNotNull(order);
			String subscription = order.getSubscription();
			Assertions.assertTrue(subscription.equals("vault") || subscription.equals("backup"));
			Assertions.assertEquals(order.getPricingPlan(), PRICING_PLAN_NAME);
			Assertions.assertNotNull(order.getPayingDid());
			Assertions.assertTrue(order.getPaymentAmount() > 0);
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
			// TODO: to do pay, please use contract
			// INFO: same for vaultSubscription and backupSubscription
			Receipt receipt = vaultSubscription.settleOrder(3).get();
			Assertions.assertNotNull(receipt);
			Assertions.assertNotNull(receipt.getReceiptId());
			Assertions.assertNotNull(receipt.getOrderId());
			String subscription = receipt.getSubscription();
			Assertions.assertTrue(subscription.equals("vault") || subscription.equals("backup"));
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
	void testGetOrderVault() {
		Assertions.assertDoesNotThrow(()->{
			Order order = vaultSubscription.getOrder(3).get();
			Assertions.assertNotNull(order);
			String subscription = order.getSubscription();
			Assertions.assertEquals(subscription, "vault");
			Assertions.assertEquals(order.getPricingPlan(), PRICING_PLAN_NAME);
			Assertions.assertNotNull(order.getPayingDid());
			Assertions.assertTrue(order.getPaymentAmount() > 0);
			Assertions.assertTrue(order.getCreateTime() > 0);
			Assertions.assertTrue(order.getExpirationTime() > 0);
			Assertions.assertNotNull(order.getReceivingAddress());
			Assertions.assertNotNull(order.getProof());
		});
	}

	@Disabled
	@Test @org.junit.jupiter.api.Order(4)
	void testGetOrderBackup() {
		Assertions.assertDoesNotThrow(()->{
			Order order = vaultSubscription.getOrder(3).get();
			Assertions.assertNotNull(order);
			String subscription = order.getSubscription();
			Assertions.assertEquals(subscription, "backup");
			Assertions.assertEquals(order.getPricingPlan(), PRICING_PLAN_NAME);
			Assertions.assertNotNull(order.getPayingDid());
			Assertions.assertTrue(order.getPaymentAmount() > 0);
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
			List<Order> orders = backupSubscription.getOrderList().get();
			Assertions.assertNotNull(orders);
			Assertions.assertFalse(orders.isEmpty());
			Order order = orders.get(0);
			String subscription = order.getSubscription();
			Assertions.assertTrue(subscription.equals("vault") || subscription.equals("backup"));
			Assertions.assertEquals(order.getPricingPlan(), PRICING_PLAN_NAME);
			Assertions.assertNotNull(order.getPayingDid());
			Assertions.assertTrue(order.getPaymentAmount() > 0);
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
			Receipt receipt = vaultSubscription.getReceipt(3).get();
			Assertions.assertNotNull(receipt);
			Assertions.assertNotNull(receipt.getReceiptId());
			Assertions.assertNotNull(receipt.getOrderId());
			String subscription = receipt.getSubscription();
			Assertions.assertTrue(subscription.equals("vault") || subscription.equals("backup"));
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
			List<Receipt> receipts = backupSubscription.getReceipts().get();
			Assertions.assertNotNull(receipts);
			Assertions.assertTrue(receipts.size() > 0);
			Receipt receipt = receipts.get(0);
			Assertions.assertNotNull(receipt.getReceiptId());
			Assertions.assertNotNull(receipt.getOrderId());
			String subscription = receipt.getSubscription();
			Assertions.assertTrue(subscription.equals("vault") || subscription.equals("backup"));
			Assertions.assertEquals(receipt.getPricingPlan(), PRICING_PLAN_NAME);
			Assertions.assertTrue(receipt.getPaymentAmount() > 0);
			Assertions.assertNotNull(receipt.getPaidDid());
			Assertions.assertTrue(receipt.getCreateTime() > 0);
			Assertions.assertNotNull(receipt.getReceivingAddress());
			Assertions.assertNotNull(receipt.getReceiptProof());
		});
	}
}
