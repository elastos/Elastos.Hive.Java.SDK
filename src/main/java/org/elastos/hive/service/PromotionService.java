package org.elastos.hive.service;

import java.util.concurrent.CompletableFuture;

public interface PromotionService {
	CompletableFuture<Void> promote();
}
