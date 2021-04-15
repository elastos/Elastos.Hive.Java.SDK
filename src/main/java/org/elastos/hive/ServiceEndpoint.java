package org.elastos.hive;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.UnauthorizedStateException;

public class ServiceEndpoint {
	private AppContext context;
	private String providerAddress;
	private ConnectionManager connectionManager;

	protected ServiceEndpoint(AppContext context, String providerAddress) {
		this.context = context;
		this.providerAddress = providerAddress;
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
