package org.elastos.hive.backup;

import org.elastos.hive.Backup;
import org.elastos.hive.service.PromotionService;

public class ServiceBuilder {
	private Backup backup;

	public ServiceBuilder(Backup backup) {
		this.backup = backup;
	}

	public PromotionService createPromotionService() {
		return new PromotionServiceRender(backup);
	}
}
