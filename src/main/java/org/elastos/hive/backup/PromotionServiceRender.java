package org.elastos.hive.backup;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.network.request.EmptyRequestBody;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.service.PromotionService;
import org.elastos.hive.vault.BaseServiceRender;
import org.elastos.hive.vault.HttpExceptionHandler;

class PromotionServiceRender extends BaseServiceRender implements PromotionService, HttpExceptionHandler {

	PromotionServiceRender(ServiceEndpoint serviceEndpoint) {
		super(serviceEndpoint);
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
