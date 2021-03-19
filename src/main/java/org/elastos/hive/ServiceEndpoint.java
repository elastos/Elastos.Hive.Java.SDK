package org.elastos.hive;

import org.elastos.hive.connection.ConnectionManager;

class ServiceEndpoint {
	@SuppressWarnings("unused")
	private AppContext context;

	private String providerAddress;
	private String userDid;
	private String targetDid;

	@SuppressWarnings("unused")
	private String targetAppDid;

	// This constructor will be embedded in the following global-grained extends:
	// - VaultSubscription;
	// - BackupSubscription;
	// - Provider;
	protected ServiceEndpoint(AppContext context, String providerAddress, String userDid) {
		this(context, providerAddress, userDid, null, null);
	}


	// This constructor will be embedded in the following service-grained extends:
	// - Vault;
	// - Backup;
	// - ScriptRunner;
	protected ServiceEndpoint(AppContext context, String providerAddress, String userDid, String targetDid, String targetAppDid) {
		this.context = context;
		this.providerAddress = providerAddress;
		this.userDid = userDid;
		this.targetDid = targetDid;
		this.targetAppDid = targetAppDid;
	}

	public String getEndpointAddress() {
		return this.providerAddress;
	}

	public String getOwnerDid() {
		return this.targetDid;
	}

	public String getUserDid() {
		return this.userDid;
	}

	public String getAppDid() {
		return null;
	}

	public String getAppInstanceDid() {
		return null;
	}

	public String getServiceDid() {
		return null;
	}

	public String getServiceInstanceDid() {
		return null;
	}

	public String getProviderAddress() {
		return this.providerAddress;
	}

	public AppContext getAppContext() {
		return this.context;
	}

	public ConnectionManager getConnectionManager() {
		return this.context.getConnectionManager();
	}
}
