package org.elastos.hive;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.UnauthorizedStateException;

public class ServiceEndpoint {
	private AppContext context;
	private String providerAddress;
	private String targetDid;
	private String targetAppDid;
	private ConnectionManager connectionManager;

	// This constructor will be embedded in the following global-grained extends:
	// - VaultSubscription;
	// - BackupSubscription;
	// - Provider;
	// - Vault;
	// - Backup;
	protected ServiceEndpoint(AppContext context, String providerAddress) {
		this(context, providerAddress, null, null);
	}

	// This constructor will be embedded in the following service-grained extends:
	// - ScriptRunner;
	protected ServiceEndpoint(AppContext context, String providerAddress, String targetDid, String targetAppDid) {
		this.context = context;
		this.providerAddress = providerAddress;
		this.targetDid = targetDid;
		this.targetAppDid = targetAppDid;
		this.connectionManager = new ConnectionManager(this);
	}

	public AppContext getAppContext() {
		return this.context;
	}

	public String getUserDid() {
		return this.context.getUserDid();
	}

	public String getProviderAddress() {
		return this.providerAddress;
	}

	public String getTargetDid() {
		return this.targetDid;
	}

	public String getTargetAppDid() {
		return this.targetAppDid;
	}

	public ConnectionManager getConnectionManager() {
		return this.connectionManager;
	}

	public String getAppDid() throws UnauthorizedStateException {
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
}
