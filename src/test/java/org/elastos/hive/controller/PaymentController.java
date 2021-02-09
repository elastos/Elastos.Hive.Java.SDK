package org.elastos.hive.controller;

import org.elastos.hive.Payment;
import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingPlan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PaymentController extends Controller {


	private static PaymentController mInstance = null;
	private Payment payment;

	public static PaymentController newInstance(Payment payment) {
		if(mInstance == null) {
			mInstance = new PaymentController(payment);
		}

		return mInstance;
	}

	private PaymentController(Payment payment) {
		this.payment = payment;
	}

	@Override
	void execute() {
		getPricingPlanByPlanName();
		payOrder();
		getUsingPricePlan();
		getPaymentVersion();
	}

	public void getPricingPlanByPlanName() {
		CompletableFuture<Boolean> future = payment.getPaymentInfo()
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
					return payment.getPricingPlan(planName);
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

	public void payOrder() {
		CompletableFuture<Boolean> future = payment.getAllOrders()
				.thenApplyAsync(orders -> {
					for(Order order : orders) {
						if(order.state().equalsIgnoreCase("wait_pay")) {
							return order.orderId();
						}
					}
					return null;
				}).thenComposeAsync(orderId -> {
					if (null == orderId) {
						return payment.placeOrder("Rookie");
					}
					return CompletableFuture.completedFuture(orderId);
				})
				.thenComposeAsync(orderId -> {
					System.out.print("Test case02 orderId ==>");
					System.out.println(orderId);

					//TODO set your paid txId
					List<String> txids = new ArrayList<>();
					return payment.payOrder(orderId, txids);
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

	
	public void getUsingPricePlan() {
		CompletableFuture<Boolean> future = payment.getUsingPricePlan()
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

	
	public void getPaymentVersion() {
		CompletableFuture<Boolean> future = payment.getPaymentVersion()
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
}
