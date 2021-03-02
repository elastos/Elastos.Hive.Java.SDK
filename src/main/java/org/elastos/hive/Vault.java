package org.elastos.hive;

public interface Vault {

	/**
	 * Get vault provider address
	 * @return	the vault provider address.
	 */
	String getProviderAddress();

	/**
	 * Get vault owner did
	 * @return	the vault owner DID.
	 */
	String getOwnerDid();

	/**
	 * Get application id
	 * @return 	the application id.
	 */
	String getAppId();

	/**
	 * Get application did
	 * @return	the application DID.
	 */
	String getAppInstanceDid();

	/**
	 * Get user did
	 * @return	the user DID.
	 */
	String getUserDid();

	/**
	 * Get the interface as database instance
	 * @return interface instance of Database.
	 */
	Database getDatabase();

	/**
	 * Get the interface as Files instance
	 * @return interface instance of Files.
	 */
	Files getFiles();

	/**
	 * Get interface as KeyValues instance
	 * @return interface instance of KeyValues
	 */
	KeyValues getKeyValues();

	/**
	 * Get interface as Scripting instance
	 * @return	interface instance of Scripting
	 */
	Scripting getScripting();

	/**
	 * clear local access token
	 */
	void revokeAccessToken();
}
