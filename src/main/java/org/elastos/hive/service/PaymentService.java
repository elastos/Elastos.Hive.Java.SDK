package org.elastos.hive.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingPlan;
import org.elastos.hive.payment.Receipt;

public interface PaymentService {
	CompletableFuture<List<PricingPlan>> getPricingPlanList();

	CompletableFuture<PricingPlan> getPricingPlan(String planName);

	CompletableFuture<Order> placeOrder(String planName);

	CompletableFuture<Order> getOrder(String orderId);

	CompletableFuture<Receipt> payOrder(String orderId, String transId);

	CompletableFuture<Receipt> getReceipt(String receiptId);

}
