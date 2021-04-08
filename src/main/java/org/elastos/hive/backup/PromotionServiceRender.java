package org.elastos.hive.backup;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.Backup;
import org.elastos.hive.network.request.EmptyRequestBody;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.service.PromotionService;
import org.elastos.hive.vault.HiveVaultRender;
import org.elastos.hive.vault.HttpExceptionHandler;

class PromotionServiceRender extends HiveVaultRender implements PromotionService, HttpExceptionHandler {

	PromotionServiceRender(Backup backup) {
		super(backup);
	}

	@Override
	public CompletableFuture<Void> promote() {
		return CompletableFuture.runAsync(() -> {
			try {
				HiveResponseBody.validateBody(
						getConnectionManager().getBackupApi()
								.activeToVault(new EmptyRequestBody())
								.execute()
								.body());
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

}
