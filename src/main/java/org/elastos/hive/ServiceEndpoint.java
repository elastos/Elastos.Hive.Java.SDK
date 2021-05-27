package org.elastos.hive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.about.AboutController;
import org.elastos.hive.about.NodeVersion;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.UnauthorizedStateException;
import org.elastos.hive.vault.ExceptionConvertor;

public class ServiceEndpoint implements ExceptionConvertor {
	private AppContext context;
	private String providerAddress;
	private ConnectionManager connectionManager;
	private String appDid;
	private String appInstanceDid;
	private String serviceInstanceDid;

	protected ServiceEndpoint(AppContext context, String providerAddress) {
		this.context = context;
		this.providerAddress = providerAddress;
		this.connectionManager = new ConnectionManager();
		this.connectionManager.attach(this);
	}

	public AppContext getAppContext() {
		return context;
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
	 * Get the end-point address of this service End-point.
	 *
	 * @return provider address
	 */
	public String getProviderAddress() {
		return providerAddress;
	}

	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	public void setAppDid(String appDid) {
		this.appDid = appDid;
	}

	/**
	 * Get the application DID in the current calling context.
	 *
	 * @return application did
	 */
	protected String getAppDid() {
		return appDid;
	}

	public void setAppInstanceDid(String appInstanceDid) {
		this.appInstanceDid = appInstanceDid;
	}

	/**
	 * Get the application instance DID in the current calling context;
	 *
	 * @return application instance did
	 */
	protected String getAppInstanceDid() {
		return appInstanceDid;
	}

	public void setServiceInstanceDid(String serviceInstanceDid) {
		this.serviceInstanceDid = serviceInstanceDid;
	}

	/**
	 * Get the remote node service instance DID where is serving the storage service.
	 *
	 * @return node service instance did
	 */
	public String getServiceInstanceDid() {
		return serviceInstanceDid;
	}

	/**
	 * Get the remote node service application DID.
	 *
	 * @return node service did
	 */
	public String getServiceDid() {
		throw new UnsupportedOperationException();
	}

	public CompletableFuture<NodeVersion> getNodeVersion() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				AboutController controller = new AboutController(this);
				return controller.getNodeVersion();
			} catch (HiveException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			} catch (RuntimeException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}

	public CompletableFuture<String> getLatestCommitId() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				AboutController controller = new AboutController(this);
				return controller.getCommitId();
			} catch (HiveException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			} catch (RuntimeException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}
}
