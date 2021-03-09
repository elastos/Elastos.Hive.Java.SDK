package org.elastos.hive.service;

import java.util.concurrent.CompletableFuture;

public interface PromotionService {
	public CompletableFuture<Void> promote();
}
