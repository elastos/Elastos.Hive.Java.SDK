package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

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
	private Version version;

	private String vaultProvider;
	private String ownerDid;
	private AuthHelper authHelper;

	Vault(AuthHelper authHelper, String vaultProvider, String ownerDid) {
		this.authHelper = authHelper;
		this.vaultProvider = vaultProvider;
		this.ownerDid = ownerDid;

		this.files = new FilesClient(authHelper);
		this.database = new DatabaseClient(authHelper);
		this.scripting = new ScriptingClient(authHelper);
		this.version = new Version(authHelper);
	}

	public CompletableFuture<String> getNodeVersion() {
		return this.version.getNodeVersion();
	}

	public CompletableFuture<String> getNodeLastCommitId() {
		return this.version.getLastCommitId();
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
