package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.Provider.Version;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.UnauthorizedStateException;
import org.elastos.hive.exception.UnsupportedMethodException;

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

	/**
	 * Get the user DID string of this serviceEndpoint.
	 * @return
	 */
	public String getUserDid() {
		return this.context.getUserDid();
	}

	/**
	 * Get the end-point address of this service End-point.
	 * @return
	 */
	public String getProviderAddress() {
		return this.providerAddress;
	}

	public ConnectionManager getConnectionManager() {
		return this.connectionManager;
	}

	/**
	 * Get the application DID in the current calling context.
	 * @return
	 */
	public String getAppDid() {
		throw new UnauthorizedStateException();
	}

	/**
	 * Get the application instance DID in the current calling context;
	 * @return
	 */
	public String getAppInstanceDid() {
		throw new UnauthorizedStateException();
	}


	/**
	 * Get the remote node service application DID.
	 * @return
	 */
	public String getServiceDid() {
		throw new UnsupportedMethodException();
	}

	/**
	 * Get the remote node service instance DID where is serving the storage service.
	 * @return
	 */
	public String getServiceInstanceDid() {
		throw new UnauthorizedStateException();
	}

	public CompletableFuture<Version> getVersion() {
		throw new UnsupportedMethodException();
	}

	public CompletableFuture<String> getLatestCommitId() {
		throw new UnsupportedMethodException();
	}
}
