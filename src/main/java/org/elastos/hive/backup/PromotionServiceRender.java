package org.elastos.hive.backup;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.network.request.EmptyRequestBody;
import org.elastos.hive.connection.HiveResponseBody;
import org.elastos.hive.service.PromotionService;
import org.elastos.hive.vault.ExceptionConvertor;

class PromotionServiceRender implements PromotionService, ExceptionConvertor {
	private ServiceEndpoint serviceEndpoint;

	PromotionServiceRender(ServiceEndpoint serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
	}

	@Override
	public CompletableFuture<Void> promote() {
		return CompletableFuture.runAsync(() -> {
			try {
				HiveResponseBody.validateBody(
						serviceEndpoint.getConnectionManager().getCallAPI()
								.activeToVault(new EmptyRequestBody())
								.execute()
								.body());
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

}
