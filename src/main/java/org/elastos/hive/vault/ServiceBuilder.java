package org.elastos.hive.vault;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.service.BackupService;
import org.elastos.hive.service.DatabaseService;
import org.elastos.hive.service.FilesService;
import org.elastos.hive.service.ScriptingService;

/**
 * Helper class to create service instance.
 */
public class ServiceBuilder {
	private ServiceEndpoint serviceEndpoint;

	/**
	 * Create by the service end point.
	 *
	 * @param serviceEndpoint The service end point.
	 */
	public ServiceBuilder(ServiceEndpoint serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
	}

	/**
	 * Create the service for the files module.
	 *
	 * @return The instance of file service.
	 */
	public FilesService createFilesService() {
		return new FilesServiceRender(serviceEndpoint);
	}

	/**
	 * Create the service for the database module.
	 *
	 * @return The instance of database service.
	 */
	public DatabaseService createDatabase() {
		return new DatabaseServiceRender(serviceEndpoint);
	}

	/**
	 * Create the service of the scripting module.
	 *
	 * @return The instance of scripting service.
	 */
	public ScriptingService createScriptingService() {
		return new ScriptingServiceRender(serviceEndpoint);
	}

	/**
	 * Create the service of the backup module.
	 *
	 * @return The instance of the backup service.
	 */
	public BackupService createBackupService() {
		return new BackupServiceRender(serviceEndpoint);
	}
}
