package org.elastos.hive.service;

import java.util.concurrent.CompletableFuture;

public interface SubscriptionService {
	<T> CompletableFuture<T> subscribe0(String pricingPlan, Class<T> type);

	CompletableFuture<Void> unsbuscribe();

	CompletableFuture<Void> activate();

	CompletableFuture<Void> deactivate();

	<T> CompletableFuture<T> checkSubscription();
}
