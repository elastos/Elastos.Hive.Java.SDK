package org.elastos.hive.service;

import java.util.concurrent.CompletableFuture;

/**
 * The promotion service is for upgrading the backup node to the vault node.
 */
public interface PromotionService {
	/**
	 * Promote the backup node to vault node by backup data.
	 *
	 * @return Void
	 */
	CompletableFuture<Void> promote();
}
