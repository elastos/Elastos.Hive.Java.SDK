package org.elastos.hive.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingPlan;
import org.elastos.hive.payment.Receipt;

public interface PaymentService {
	public CompletableFuture<List<PricingPlan>> getPringPlanList();

	public CompletableFuture<PricingPlan> getPricingPlan(String planName);

	public CompletableFuture<Order> placeOrder(String planName);

	public CompletableFuture<Order> getOrder(String orderId);

	public CompletableFuture<Receipt> payOrder(String orderId, String transId);

	public CompletableFuture<Receipt> getReceipt(String receiptId);

}
