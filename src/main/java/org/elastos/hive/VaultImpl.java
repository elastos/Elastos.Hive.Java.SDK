package org.elastos.hive;

/**
 * Vault class
 *      Provide basic information of vault.
 *      Provide interface instances of file, database, KeyValue and scripting.
 */
public class VaultImpl implements Vault {
	private Files files;
	private Database database;
	private Scripting scripting;
	private KeyValues keyValues;

	private String providerAddress;
	private String ownerDid;
	private AuthHelper authHelper;

	VaultImpl(AuthHelper authHelper, String providerAddress, String ownerDid) {
		this.authHelper = authHelper;
		this.providerAddress = providerAddress;
		this.ownerDid = ownerDid;

		this.files = new FilesImpl(authHelper);
		this.database = new DatabaseImpl(authHelper);
		this.scripting = new ScriptingImpl(authHelper);
	}

	@Override
	public String getProviderAddress() {
		return this.providerAddress;
	}

	@Override
	public String getOwnerDid() {
		return this.ownerDid;
	}

	@Override
	public String getAppId() {
		return this.authHelper.appId();
	}

	@Override
	public String getAppInstanceDid() {
		return this.authHelper.appInstanceDid();
	}

	@Override
	public String getUserDid() {
		return this.authHelper.userDid();
	}

	@Override
	public Database getDatabase() {
		return this.database;
	}

	@Override
	public Files getFiles() {
		return this.files;
	}

	@Override
	public KeyValues getKeyValues() {
		return this.keyValues;
	}

	@Override
	public Scripting getScripting() {
		return this.scripting;
	}

	@Override
	public void revokeAccessToken() {
		authHelper.removeToken();
	}
}
