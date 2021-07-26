package org.elastos.hive.backup;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.service.PromotionService;

/**
 * The service builder for the services of the backup hive node.
 *
 * <p>TODO: to be implemented.</p>
 */
public class ServiceBuilder {
	private ServiceEndpoint backup;

	public ServiceBuilder(ServiceEndpoint backup) {
		this.backup = backup;
	}

	public PromotionService createPromotionService() {
		return new PromotionServiceRender(backup);
	}
}
