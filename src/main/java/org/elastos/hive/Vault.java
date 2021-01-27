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

	private String providerAddress;
	private String ownerDid;
	private AuthHelper authHelper;

	Vault(AuthHelper authHelper, String providerAddress, String ownerDid) {
		this.authHelper = authHelper;
		this.providerAddress = providerAddress;
		this.ownerDid = ownerDid;

		this.files = new FilesImpl(authHelper);
		this.database = new DatabaseImpl(authHelper);
		this.scripting = new ScriptingImpl(authHelper);
		this.version = new VersionImpl(authHelper);
	}

	public CompletableFuture<String> getNodeVersion() {
		return this.version.getVersion();
	}

	public CompletableFuture<String> getNodeLastCommitId() {
		return this.version.getLastCommitId();
	}

	/**
	 * Get vault provider address
	 * @return	the vault provider address.
	 */
	public String getProviderAddress() {
		return this.providerAddress;
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
		return this.authHelper.appId();
	}

	/**
	 * Get application did
	 * @return	the application DID.
	 */
	public String getAppInstanceDid() {
		return this.authHelper.appInstanceDid();
	}

	/**
	 * Get user did
	 * @return	the user DID.
	 */
	public String getUserDid() {
		return this.authHelper.userDid();
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

	public void revokeAccessToken() {
		authHelper.removeToken();
	}
}
