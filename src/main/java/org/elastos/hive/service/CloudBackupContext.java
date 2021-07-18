package org.elastos.hive.service;

/**
 * Cloud backup context is used for the user to backup the vault data to the cloud service,
 * such as google driver, etc.
 */
public abstract class CloudBackupContext implements BackupContext {
	@Override
	public String getParameter(String parameter) {
		switch (parameter) {
			case "clientId":
				return getClientId();

			case "redirectUrl":
				return getRedirectUrl();

			case "scope":
				return getAppScope();

			default:
				break;
		}

		return null;
	}

	/**
	 * Get the client ID for access the cloud service.
	 *
	 * @return Client ID
	 */
	public abstract String getClientId();

	/**
	 * Get the redirect URL.
	 *
	 * @return Redirect URL.
	 */
	public abstract String getRedirectUrl();

	/**
	 * Get the application scope.
	 *
	 * @return The application scope.
	 */
	public abstract String getAppScope();
}
