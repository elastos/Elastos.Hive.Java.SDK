package org.elastos.hive;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.UnauthorizedStateException;

class ServiceEndpoint {
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
		this(context, providerAddress, userDid, userDid, null);
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

	public String getAddress() {
		return this.providerAddress;
	}

	public String getOwnerDid() {
		return this.targetDid;
	}

	public String getUserDid() {
		return this.userDid;
	}

	public String getAppDid() throws UnauthorizedStateException {
		if (this.context.getConnectionManager() == null)
			throw new UnauthorizedStateException("This instance has not been authorized by User");

		// TODO:
		return null;
	}

	public String getAppInstanceDid() throws UnauthorizedStateException {
		return null;
	}

	public String getServiceDid() throws UnauthorizedStateException {
		return null;
	}

	public String getServiceInstanceDid() throws UnauthorizedStateException {
		return null;
	}

	public AppContext getAppContext() {
		return this.context;
	}
}
