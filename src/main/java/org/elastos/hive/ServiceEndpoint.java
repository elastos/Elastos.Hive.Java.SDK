package org.elastos.hive;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import org.elastos.did.jwt.Claims;
import org.elastos.did.jwt.JwtParserBuilder;
import org.elastos.hive.connection.NodeRPCConnection;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.connection.auth.AccessToken;
import org.elastos.hive.connection.auth.BridgeHandler;
import org.elastos.hive.endpoint.AboutController;
import org.elastos.hive.endpoint.NodeInfo;
import org.elastos.hive.endpoint.NodeVersion;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.NotImplementedException;

/**
 * The service end-point represents the service provides some API functions. It supports:
 *
 * <ul>
 * <li>Access token management.</li>
 * <li>Local cache for the access token.</li>
 * <li>The service DID of the hive node.</li>
 * <li>The provider address.</li>
 * </ul>
 *
 * <p>The service end-point is just like the map of the hive node. The application can communicate
 * 		with the hive node APIs by its sub-class.</p>
 */
public class ServiceEndpoint extends NodeRPCConnection {
	private AppContext context;
	private String providerAddress;

	private String appDid;
	private String appInstanceDid;
	private String serviceInstanceDid;

	private AccessToken accessToken;
	private DataStorage dataStorage;

	/**
	 * Create by the application context, and the address of the provider.
	 *
	 * @param context The application context.
	 * @param providerAddress The address of the provider.
	 */
	protected ServiceEndpoint(AppContext context, String providerAddress) {
		if (context == null)
			throw new IllegalArgumentException("Empty context or provider address parameter");

		this.context = context;
		this.providerAddress = providerAddress;

		String dataDir = context.getAppContextProvider().getLocalDataDir();
		if (!dataDir.endsWith(File.separator))
			dataDir += File.separator;

		this.dataStorage = new FileStorage(dataDir, context.getUserDid());
		this.accessToken = new AccessToken(this, dataStorage, new BridgeHandler() {
			private WeakReference<ServiceEndpoint> weakref;

			@Override
			public void flush(String value) {
				try {
					ServiceEndpoint endpoint = weakref.get();
					Claims claims;

					claims = new JwtParserBuilder().setAllowedClockSkewSeconds(300).build().parseClaimsJws(value).getBody();
					endpoint.flushDids(claims.getAudience(), claims.getIssuer());

				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}

			BridgeHandler setTarget(ServiceEndpoint endpoint) {
				this.weakref = new WeakReference<>(endpoint);
				return this;
			}

			@Override
			public Object target() {
				return weakref.get();
			}

		}.setTarget(this));
	}

	/**
	 * Get the application context.
	 *
	 * @return The application context.
	 */
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
		if (providerAddress == null) {
			try {
				providerAddress = context.getProviderAddress().get();
			} catch (InterruptedException | ExecutionException  e) {
				throw new RuntimeException("Failed to get the provider address from the user did.");
			}
		}
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

	private void flushDids(String appInstanceDId, String serviceInstanceDid) {
		this.appInstanceDid = appInstanceDId;
		this.serviceInstanceDid = serviceInstanceDid;
	}

	/**
	 * Get the instance of the data storage.
	 *
	 * @return The instance of the data storage.
	 */
	public DataStorage getStorage() {
		return dataStorage;
	}

	/**
	 * Refresh the access token. This will do remote refresh if not exist.
	 *
	 * @throws NodeRPCException See {@link org.elastos.hive.connection.NodeRPCException}
	 */
	public void refreshAccessToken() throws NodeRPCException {
		accessToken.fetch();
	}

	@Override
	protected AccessToken getAccessToken() {
		return accessToken;
	}

	/**
	 * Get the version of the hive node.
	 *
	 * @return The version of the hive node.
	 */
	public CompletableFuture<NodeVersion> getNodeVersion() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return new AboutController(this).getNodeVersion();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	/**
	 * Get the last commit ID of the hive node.
	 *
	 * @return The last commit ID.
	 */
	public CompletableFuture<String> getLatestCommitId() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return new AboutController(this).getCommitId();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	/**
	 * Get the information of the hive node.
	 *
	 * @return The information.
	 */
	public CompletableFuture<NodeInfo> getNodeInfo() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return new AboutController(this).getNodeInfo();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}
}
