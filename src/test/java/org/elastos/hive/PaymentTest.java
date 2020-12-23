package org.elastos.hive;

import org.elastos.hive.didhelper.AppInstanceFactory;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentTest {

	private static Payment paymentApi;
	private static final String planName = "Free";
	private static final String priceName = "Rookie";
	private static String orderId = "5fb5f1be9284ff39688ea77e";

	@Test
	public void test01_getPaymentInfo() {
		CompletableFuture<Boolean> future = paymentApi.getPaymentInfo()
				.handleAsync((pricingInfo, throwable) -> (throwable == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test02_getPricingPlan() {
		CompletableFuture<Boolean> future = paymentApi.getPricingPlan(planName)
				.handleAsync((pricingPlan, throwable) -> (throwable == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test03_placeOrder() {
		CompletableFuture<Boolean> future = paymentApi.placeOrder(priceName)
				.handleAsync((orderId, throwable) -> (throwable == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test04_payOrder() {
		List<String> txids = new ArrayList<>();
		CompletableFuture<Boolean> future = paymentApi.payOrder(orderId, txids)
				.handleAsync((aBoolean, throwable) -> (throwable == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test05_getOrder() {
		CompletableFuture<Boolean> future = paymentApi.getOrder(orderId)
				.handleAsync((order, throwable) -> (throwable == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test06_getAllOrders() {
		CompletableFuture<Boolean> future = paymentApi.getAllOrders()
				.handleAsync((orders, throwable) -> (throwable == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test07_getUsingPricePlan() {
		CompletableFuture<Boolean> future = paymentApi.getUsingPricePlan()
				.handleAsync((usingPlan, throwable) -> (throwable == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test07_getPaymentVersion() {
		CompletableFuture<Boolean> future = paymentApi.getPaymentVersion()
				.handleAsync((version, throwable) -> (throwable == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	@BeforeClass
	public static void setUp() {
		Vault vault = AppInstanceFactory.getUser2().getVault();
		paymentApi = vault.getPayment();
	}
}
