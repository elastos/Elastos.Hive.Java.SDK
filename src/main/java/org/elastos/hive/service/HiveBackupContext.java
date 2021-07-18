package org.elastos.hive.service;

/**
 * The backup context for hive node server.
 */
public abstract class HiveBackupContext implements BackupContext {
	@Override
	public String getParameter(String parameter) {
		switch (parameter) {
			case "targetAddress":
				return this.getTargetProviderAddress();

			case "targetServiceDid":
				return this.getTargetServiceDid();

			default:
				break;
		}
		return null;
	}

	/**
	 * Get the host URL of the backup server.
	 *
	 * @return Host URL.
	 */
	public abstract String getTargetProviderAddress();

	/**
	 * Get the service DID of the backup server.
	 *
	 * @return The service DID.
	 */
	public abstract String getTargetServiceDid();
}
