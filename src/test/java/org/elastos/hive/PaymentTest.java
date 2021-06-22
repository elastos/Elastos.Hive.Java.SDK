package org.elastos.hive;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.didhelper.AppInstanceFactory;
import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingPlan;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentTest {

	private static Payment paymentApi;

	@Test
	public void test01_getPricingPlanByPlanName() {
		CompletableFuture<Boolean> future = paymentApi.getPaymentInfo()
				.thenApplyAsync(pricingInfo -> {
					try {
						System.out.print("Test case01 PaymentInfo ==>");
						System.out.println(pricingInfo.serialize());
					} catch (IOException e) {
						e.printStackTrace();
					}
					List<PricingPlan> pricePlans = pricingInfo.pricingPlans();
					if (pricePlans.size() <= 0) {
						fail();
						return null;
					}
					for (PricingPlan pricingPlan : pricePlans) {
						if (pricingPlan.amount() > 0) {
							return pricingPlan;
						}
					}
					return null;
				})
				.thenCompose(pricingInfo -> {
					if (pricingInfo == null) {
						fail();
					}
					String planName = pricingInfo.name();
					return paymentApi.getPricingPlan(planName);
				}).handleAsync((pricingPlan, throwable) -> {
					if(throwable != null) {
						return false;
					}
					try {
						System.out.print("Test case01 PricingPlan ==>");
						System.out.println(pricingPlan.serialize());
					} catch (IOException e) {
						e.printStackTrace();
					}
					return true;
				});

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test02_payOrder() {
		CompletableFuture<Boolean> future = paymentApi.getAllOrders()
				.thenApplyAsync(orders -> {
					for(Order order : orders) {
						if(order.state().equalsIgnoreCase("wait_pay")) {
							return order.orderId();
						}
					}
					return null;
				}).thenComposeAsync(orderId -> {
					if (null == orderId) {
						return paymentApi.placeOrder("Rookie");
					}
					return CompletableFuture.completedFuture(orderId);
				})
				.thenComposeAsync(orderId -> {
					System.out.print("Test case02 orderId ==>");
					System.out.println(orderId);

					//TODO set your paid txId
					List<String> txids = new ArrayList<>();
					return paymentApi.payOrder(orderId, txids);
				})
				.handle((aBoolean, throwable) -> (throwable == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test03_getUsingPricePlan() {
		CompletableFuture<Boolean> future = paymentApi.getUsingPricePlan()
				.handleAsync((usingPlan, throwable) -> (throwable == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test04_getPaymentVersion() {
		CompletableFuture<Boolean> future = paymentApi.getPaymentVersion()
				.handleAsync((version, throwable) -> (throwable == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@BeforeClass
	public static void setUp() {
		Vault vault = AppInstanceFactory.configSelector().getVault();
		paymentApi = vault.getPayment();
	}
}
