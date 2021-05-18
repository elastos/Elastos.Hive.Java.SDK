package org.elastos.hive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.UnauthorizedStateException;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.vault.ExceptionConvertor;

public class ServiceEndpoint implements ExceptionConvertor {
	private AppContext context;
	private String providerAddress;
	private ConnectionManager connectionManager;
	private String appInstanceDid;
	private String serviceInstanceDid;

	protected ServiceEndpoint(AppContext context, String providerAddress) {
		this.context = context;
		this.providerAddress = providerAddress;
		this.connectionManager = new ConnectionManager(this);
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

	/**
	 * Get the application DID in the current calling context.
	 *
	 * @return application did
	 */
	protected String getAppDid() {
		return getAppContext().getAppContextProvider().getAppDid();
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
		throw new UnauthorizedStateException();
	}

	public CompletableFuture<Version> getVersion() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				String version = HiveResponseBody.validateBody(
						connectionManager.getAboutAPI().version().execute().body())
					.getVersion();

				return getVersionByStr(version);
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
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
				return HiveResponseBody.validateBody(
		                connectionManager.getAboutAPI()
		                        .commitHash()
		                        .execute()
		                        .body()).getCommitHash();
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
    }

}
