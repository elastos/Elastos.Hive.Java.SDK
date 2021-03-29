package org.elastos.hive.service;

import java.util.concurrent.CompletableFuture;

public interface SubscriptionService<T> {
	CompletableFuture<T> subscribe(String pricingPlan);

	CompletableFuture<Void> unsubscribe();

	CompletableFuture<Void> activate();

	CompletableFuture<Void> deactivate();

	CompletableFuture<T> checkSubscription();
}
