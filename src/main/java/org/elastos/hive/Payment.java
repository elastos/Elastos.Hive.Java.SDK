package org.elastos.hive;

import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingPlan;
import org.elastos.hive.payment.UsingPlan;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Payment {

	/**
	 * get vault's pricing plan informations
	 * @return PricingPlan list
	 * @see org.elastos.hive.payment.PricingPlan
	 */
	CompletableFuture<List<PricingPlan>> getAllPricingPlans();

	/**
	 * get vault pricing plan information by plan name
	 * @param planName plan name
	 * @return the instance of PricingPlan
	 * @see org.elastos.hive.payment.PricingPlan
	 */
	CompletableFuture<PricingPlan> getPricingPlan(String planName);

	/**
	 * create a order of pricing plan
	 * @param priceName
	 * @return
	 */
	CompletableFuture<String> placeOrder(String priceName);

	/**
	 * pay for  pricing plan order
	 * @param orderId
	 * @param txids
	 * @return
	 */
	CompletableFuture<Boolean> payOrder(String orderId, List<String> txids);

	/**
	 * start vault service free trial
	 * @return
	 */
	CompletableFuture<Boolean> useTrial();

	/**
	 * Get order information of vault service purchase
	 * @param orderId
	 * @return
	 */
	CompletableFuture<Order> getOrder(String orderId);

	/**
	 * Get user order information list of vault service purchase
	 * @return
	 */
	CompletableFuture<List<Order>> getAllOrders();

	/**
	 * Get using price plan
	 * @return
	 */
	CompletableFuture<UsingPlan> getUsingPricePlan();

	/**
	 *
	 * @return
	 */
	CompletableFuture<String> getPaymentVersion();
}
