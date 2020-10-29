package org.elastos.hive;

import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingPlan;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Payment {

	/**
	 * get vault packages's informations
	 * @return
	 */
	CompletableFuture<List<PricingPlan>> getAllPricingPlans();

	/**
	 * get vault package's information by plan name
	 * @param planName
	 * @return
	 */
	CompletableFuture<PricingPlan> getPricingPlan(String planName);

	/**
	 * create a package order
	 * @param packageName
	 * @param priceName
	 * @return
	 */
	CompletableFuture<Boolean> placeOrder(String packageName, String priceName);

	/**
	 * Pay vault service package order
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
	 * Get user vault service info
	 * @return
	 */
	CompletableFuture<PricingPlan> getUsingPricePlan();

}
