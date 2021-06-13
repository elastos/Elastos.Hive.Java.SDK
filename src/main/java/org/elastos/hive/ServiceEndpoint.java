package org.elastos.hive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.did.jwt.Claims;
import org.elastos.did.jwt.JwtParserBuilder;
import org.elastos.hive.about.AboutController;
import org.elastos.hive.about.NodeVersion;
import org.elastos.hive.auth.AccessToken;
import org.elastos.hive.auth.UpdateHandler;
import org.elastos.hive.connection.NodeRPCConnection;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.NotImplementedException;
import org.elastos.hive.storage.DataStorage;

public class ServiceEndpoint extends NodeRPCConnection {
	private AppContext context;
	private String providerAddress;

	private String appDid;
	private String appInstanceDid;
	private String serviceInstanceDid;

	private DataStorage storage;
	private AccessToken accessToken;

	protected ServiceEndpoint(AppContext context, String providerAddress) {
		if (context == null || providerAddress == null)
			throw new IllegalArgumentException("Empty context or provider address parameter");

		this.context = context;
		this.providerAddress = providerAddress;
		this.storage = context.dataStorage();
		this.accessToken = new AccessToken(this, this.storage, new UpdateHandler() {
			private ServiceEndpoint endpoint;

			@Override
			public void update(String value) {
				Claims claims;
				try {
					claims = new JwtParserBuilder().build().parseClaimsJws(value).getBody();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}

				endpoint.setAppInstanceDid(claims.getIssuer());
				endpoint.setServiceInstanceDid(claims.getSubject());
			}

			UpdateHandler setTarget(ServiceEndpoint endpoint) {
				this.endpoint = endpoint;
				return this;
			}
		}.setTarget(this));
	}

	public AppContext getAppContext() {
		return context;
	}

	/**
	 * Get the end-point address of this service End-point.
	 *
	 * @return provider address
	 */
	@Override
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
		throw new NotImplementedException();
	}

	/**
	 * Get the remote node service instance DID where is serving the storage service.
	 *
	 * @return node service instance did
	 */
	public String getServiceInstanceDid() {
		return serviceInstanceDid;
	}

	private void setAppInstanceDid(String appInstanceDid) {
		this.appInstanceDid = appInstanceDid;
	}

	private void setServiceInstanceDid(String serviceInstanceDid) {
		this.serviceInstanceDid = serviceInstanceDid;
	}

	public DataStorage getStorage() {
		return storage;
	}

	@Override
	protected AccessToken getAccessToken() {
		return accessToken;
	}

	public CompletableFuture<NodeVersion> getNodeVersion() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return new AboutController(this).getNodeVersion();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	public CompletableFuture<String> getLatestCommitId() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return new AboutController(this).getCommitId();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}
}
