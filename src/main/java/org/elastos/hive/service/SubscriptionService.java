package org.elastos.hive.service;

import java.util.concurrent.CompletableFuture;

/**
 * Subscription is for using the functions of the vault service.
 * Before using it, subscribe request MUST be called.
 *
 * @param <T> Vault information for specific vault server.
 */
public interface SubscriptionService<T> {
	/**
	 * Subscript for using vault relating service.
	 *
	 * @return Vault service information.
	 */
	CompletableFuture<T> subscribe();

	/**
	 * Unsubscribe vault service. After this, user can't use it.
	 *
	 * @return Void
	 */
	CompletableFuture<Void> unsubscribe();

	/**
	 * Activate vault service for using.
	 *
	 * @return Void.
	 */
	CompletableFuture<Void> activate();

	/**
	 * Deactivate vault service for restrict access it later.
	 *
	 * @return Void
	 */
	CompletableFuture<Void> deactivate();

	/**
	 * Check the subscription information.
	 *
	 * @return Subscription information for vault relating service.
	 */
	CompletableFuture<T> checkSubscription();
}
