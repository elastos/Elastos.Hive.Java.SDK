package org.elastos.hive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.about.AboutController;
import org.elastos.hive.about.NodeVersion;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;

public class ServiceEndpoint {
	private AppContext context;
	private String providerAddress;
	private ConnectionManager connectionManager;
	private String appDid;
	private String appInstanceDid;
	private String serviceInstanceDid;

	protected ServiceEndpoint(AppContext context, String providerAddress) {
		if (context == null || providerAddress == null)
			throw new IllegalArgumentException("Empty context or provider address parameter");

		this.context = context;
		this.providerAddress = providerAddress;
		this.connectionManager = new ConnectionManager();
		this.connectionManager.attach(this);
	}

	public AppContext getAppContext() {
		return context;
	}

	/**
	 * Get the end-point address of this service End-point.
	 *
	 * @return provider address
	 */
	public String getProviderAddress() {
		return providerAddress;
	}

	/**
	 * Get the user DID string of this serviceEndpoint.
	 *
	 * @return user did
	 */
	public String getUserDid() {
		return context.getUserDid();
	}

	/**
	 * Get the application DID in the current calling context.
	 *
	 * @return application did
	 */
	public String getAppDid() {
		return appDid;
	}

	/**
	 * Get the application instance DID in the current calling context;
	 *
	 * @return application instance did
	 */
	public String getAppInstanceDid() {
		return appInstanceDid;
	}

	/**
	 * Get the remote node service application DID.
	 *
	 * @return node service did
	 */
	public String getServiceDid() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get the remote node service instance DID where is serving the storage service.
	 *
	 * @return node service instance did
	 */
	public String getServiceInstanceDid() {
		return serviceInstanceDid;
	}

	// TODO: make it implicit
	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	/*
	public void setAppDid(String appDid) {
		this.appDid = appDid;
	}*/

	// TODO: make it implicit
	public void setAppInstanceDid(String appInstanceDid) {
		this.appInstanceDid = appInstanceDid;
	}

	// TODO: make it implicit
	public void setServiceInstanceDid(String serviceInstanceDid) {
		this.serviceInstanceDid = serviceInstanceDid;
	}

	public CompletableFuture<NodeVersion> getNodeVersion() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return new AboutController(connectionManager).getNodeVersion();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	public CompletableFuture<String> getLatestCommitId() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return new AboutController(connectionManager).getCommitId();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}
}
