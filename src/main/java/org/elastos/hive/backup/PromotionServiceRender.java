package org.elastos.hive.backup;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.service.PromotionService;
import org.elastos.hive.vault.ExceptionConvertor;
import org.elastos.hive.vault.backup.BackupController;

class PromotionServiceRender implements PromotionService, ExceptionConvertor {
	private BackupController controller;

	PromotionServiceRender(ServiceEndpoint serviceEndpoint) {
		controller = new BackupController(serviceEndpoint);
	}

	@Override
	public CompletableFuture<Void> promote() {
		return CompletableFuture.runAsync(() -> {
			try {
				controller.promote();
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

}
