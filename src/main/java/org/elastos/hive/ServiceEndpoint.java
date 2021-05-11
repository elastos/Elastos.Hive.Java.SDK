package org.elastos.hive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.UnauthorizedStateException;
import org.elastos.hive.exception.UnsupportedMethodException;
import org.elastos.hive.vault.HttpExceptionHandler;
import org.elastos.hive.vault.NodeManageServiceRender;

public class ServiceEndpoint implements HttpExceptionHandler {
	private AppContext context;
	private String providerAddress;
	private ConnectionManager connectionManager;
	private NodeManageServiceRender nodeManageService;

	protected ServiceEndpoint(AppContext context, String providerAddress) {
		this.context = context;
		this.providerAddress = providerAddress;
		this.connectionManager = new ConnectionManager(this);
		this.nodeManageService = new NodeManageServiceRender(this);
	}

	public AppContext getAppContext() {
		return this.context;
	}

	/**
	 * Get the user DID string of this serviceEndpoint.
	 *
	 * @return user did
	 */
	public String getUserDid() {
		return this.context.getUserDid();
	}

	/**
	 * Get the end-point address of this service End-point.
	 *
	 * @return provider address
	 */
	public String getProviderAddress() {
		return this.providerAddress;
	}

	public ConnectionManager getConnectionManager() {
		return this.connectionManager;
	}

	/**
	 * Get the application DID in the current calling context.
	 *
	 * @return application did
	 */
	protected String getAppDid() {
		throw new UnauthorizedStateException();
	}

	/**
	 * Get the application instance DID in the current calling context;
	 *
	 * @return application instance did
	 */
	protected String getAppInstanceDid() {
		throw new UnauthorizedStateException();
	}


	/**
	 * Get the remote node service application DID.
	 *
	 * @return node service did
	 */
	public String getServiceDid() {
		throw new UnsupportedMethodException();
	}

	/**
	 * Get the remote node service instance DID where is serving the storage service.
	 *
	 * @return node service instance did
	 */
	protected String getServiceInstanceDid() {
		throw new UnauthorizedStateException();
	}

	public CompletableFuture<Version> getVersion() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return getVersionByStr(nodeManageService.getVersion());
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	private Version getVersionByStr(String version) {
		// TODO: Required version number is *.*.*, such as 1.0.12
		return new Version();
	}

	public CompletableFuture<String> getLatestCommitId() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return nodeManageService.getCommitHash();
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}
}
