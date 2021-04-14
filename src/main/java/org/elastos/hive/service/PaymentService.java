package org.elastos.hive.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingPlan;
import org.elastos.hive.payment.Receipt;

public interface PaymentService {
	/**
	 * Get pricing plan list from vault and backup service,
	 * such as more storage usage, backup service support, etc.
	 *
	 * @return the list of pricing plans
	 */
	CompletableFuture<List<PricingPlan>> getPricingPlanList();

	/**
	 * Get a pricing plan by name. Every pricing plan has a name with which we can do
	 * the corresponding payment operation.
	 *
	 * @param planName the name of the pricing plan
	 * @return pricing plan
	 */
	CompletableFuture<PricingPlan> getPricingPlan(String planName);

	/**
	 * Make an order for the pricing plan named with planName.
	 *
	 * @param planName the name of the pricing plan
	 * @return the corresponding order details.
	 */
	CompletableFuture<Order> placeOrder(String planName);

	/**
	 * Get order information by order id.
	 *
	 * @param orderId order id
	 * @return the corresponding order details.
	 */
	CompletableFuture<Order> getOrder(String orderId);

	/**
	 * Pay for the order made before.
	 *
	 * @param orderId order id
	 * @param transIds payment transaction ids.
	 * @return receipt details.
	 */
	CompletableFuture<Receipt> payOrder(String orderId, List<String> transIds);

	/**
	 * Get receipt details by receipt id.
	 *
	 * @param receiptId receipt id.
	 * @return receipt details.
	 */
	CompletableFuture<Receipt> getReceipt(String receiptId);
}
