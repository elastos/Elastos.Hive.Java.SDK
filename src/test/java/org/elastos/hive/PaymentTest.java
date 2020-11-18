package org.elastos.hive;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentTest {

	private static Payment paymentApi;
	private static final String planName = "Free";
	private static final String priceName = "Rookie";
	private static String orderId = "5fab73d41f002debe1cb203a";

	@Test
	public void test01_getPaymentInfo() throws ExecutionException, InterruptedException {
		paymentApi.getPaymentInfo().whenComplete((paymentInfo, throwable) -> {
			assertNull(throwable);
			assertNotNull(paymentInfo);
		}).get();
	}

	@Test
	public void test02_getPricingPlan() throws ExecutionException, InterruptedException {
		paymentApi.getPricingPlan(planName).whenComplete((pricingPlan, throwable) -> {
			assertNull(throwable);
			assertNotNull(pricingPlan);
			assertTrue(planName.equals(pricingPlan.name()));
		}).get();

	}

//	@Test
//	public void test03_placeOrder() throws ExecutionException, InterruptedException {
//		paymentApi.placeOrder(priceName).whenComplete((orderId, throwable) -> {
//			assertNull(throwable);
//			assertNotNull(orderId);
//			this.orderId = orderId;
//		}).get();
//	}

//	@Test
//	public void test04_payOrder() throws ExecutionException, InterruptedException {
//		List<String> txids = new ArrayList<>();
//		txids.add("xxxxxxxxxxx");
//		paymentApi.payOrder(orderId, txids).whenComplete((orderId, throwable) -> {
//			assertNull(throwable);
//			assertNotNull(orderId);
//		}).get();
//	}

	@Test
	public void test05_getOrder() throws ExecutionException, InterruptedException {
		paymentApi.getOrder(orderId).whenComplete((order, throwable) -> {
			assertNull(throwable);
			assertNotNull(order);
		}).get();
	}

	@Test
	public void test06_getAllOrders() throws ExecutionException, InterruptedException {
		paymentApi.getAllOrders().whenComplete((orders, throwable) -> {
			assertNull(throwable);
			assertNotNull(orders);
		}).get();
	}

	@Test
	public void test07_getUsingPricePlan() throws ExecutionException, InterruptedException {
		paymentApi.getUsingPricePlan().whenComplete((using, throwable) -> {
			assertNull(throwable);
			assertNotNull(using);
		}).get();
	}

	@Test
	public void test07_getPaymentVersion() throws ExecutionException, InterruptedException {
		paymentApi.getPaymentVersion().whenComplete((version, throwable) -> {
			assertNull(throwable);
			assertNotNull(version);
		}).get();
	}

	@BeforeClass
	public static void setUp() {
		Vault vault = UserFactory.createUser2().getVault();
		paymentApi = vault.getPayment();
	}
}
