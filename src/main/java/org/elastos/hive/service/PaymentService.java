package org.elastos.hive.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingPlan;
import org.elastos.hive.payment.Receipt;

public interface PaymentService {
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
