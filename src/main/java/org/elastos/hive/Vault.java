package org.elastos.hive;

import org.elastos.hive.vault.AuthHelper;
import org.elastos.hive.vault.DatabaseClient;
import org.elastos.hive.vault.FileClient;
import org.elastos.hive.vault.ScriptClient;

/**
 * Vault class
 *      Provide basic information of vault.
 *      Provide interface instances of file, database, KeyValue and scripting.
 */
public class Vault {

	private Files files;
	private Database database;
	private Scripting scripting;
	private KeyValues keyValues;

	private String vaultProvider;
	private String ownerDid;
	private AuthHelper authHelper;

	/**
	 * Vault constructor
	 *
	 * @param authHelper
	 *          sign in，authorize and cloud sync helper class instance
	 * @param vaultProvider
	 *          vault server provider address
	 * @param ownerDid
	 *          vault provider did
	 */
	public Vault(AuthHelper authHelper, String vaultProvider, String ownerDid) {
		this.authHelper = authHelper;
		this.vaultProvider = vaultProvider;
		this.ownerDid = ownerDid;

		this.files = new FileClient(authHelper);
		this.database = new DatabaseClient(authHelper);
		this.scripting = new ScriptClient(authHelper);
	}

	/**
	 * Get vault provider address
	 * @return	the vault provider address.
	 */
	public String getProviderAddress() {
		return this.vaultProvider;
	}

	/**
	 * Get vault owner did
	 * @return	the vault owner DID.
	 */
	public String getOwnerDid() {
		return this.ownerDid;
	}

	/**
	 * Get application id
	 * @return 	the application id.
	 */
	public String getAppId() {
		return this.authHelper.getAppId();
	}

	/**
	 * Get application did
	 * @return	the application DID.
	 */
	public String getAppInstanceDid() {
		return this.authHelper.getAppInstanceDid();
	}

	/**
	 * Get user did
	 * @return	the user DID.
	 */
	public String getUserDid() {
		return this.authHelper.getUserDid();
	}

	/**
	 * Get the interface as database instance
	 * @return interface instance of Database.
	 */
	public Database getDatabase() {
		return this.database;
	}

	/**
	 * Get the interface as Files instance
	 * @return interface instance of Files.
	 */
	public Files getFiles() {
		return this.files;
	}

	/**
	 * Get interface as KeyValues instance
	 * @return interface instance of KeyValues
	 */
	public KeyValues getKeyValues() {
		return this.keyValues;
	}

	/**
	 * Get interface as Scripting instance
	 * @return	interface instance of Scripting
	 */
	public Scripting getScripting() {
		return this.scripting;
	}
}
