package org.elastos.hive.service;

import java.util.concurrent.CompletableFuture;

public interface SubscriptionService {
	<T> CompletableFuture<T> subscribe(String pricingPlan, Class<T> type);

	CompletableFuture<Void> unsubscribe();

	CompletableFuture<Void> activate();

	CompletableFuture<Void> deactivate();

	<T> CompletableFuture<T> checkSubscription();
}
