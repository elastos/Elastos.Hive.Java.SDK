package org.elastos.hive.connection.auth;

import org.elastos.did.jwt.*;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.DataStorage;

/**
 * The access token is made by hive node and represents the user DID and the application DID.
 *
 * <p>Some of the node APIs requires access token when handling request.</p>
 */
public class AccessToken implements CodeFetcher {
	private String jwtCode;
	private CodeFetcher remoteFetcher;
	private DataStorage storage;
	private BridgeHandler bridge;

	/**
	 * Create the access token by service end point, data storage, and bridge handler.
	 *
	 * @param endpoint The service end point.
	 * @param storage The data storage which is used to save the access token.
	 * @param bridge The bridge handle is used for caller to do sth when getting the access token.
	 */
	public AccessToken(ServiceEndpoint endpoint, DataStorage storage, BridgeHandler bridge) {
		remoteFetcher = new RemoteFetcher(endpoint);
		this.storage = storage;
		this.bridge = bridge;
	}

	/**
	 * Get the access token without exception.
	 *
	 * @return null if not exists.
	 */
	public String getCanonicalizedAccessToken() {
		try {
			String token = fetch();
			return "token " + token;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String fetch() throws NodeRPCException {
		if (jwtCode != null)
			return jwtCode;

		jwtCode = restoreToken();
		if (jwtCode == null) {
			jwtCode = remoteFetcher.fetch();
			if (jwtCode != null) {
				bridge.flush(jwtCode);
				saveToken(jwtCode);
			}
		} else {
			bridge.flush(jwtCode);
		}
		return jwtCode;
	}

	@Override
	public void invalidate() {
		clearToken();
	}

	private String restoreToken() {
		ServiceEndpoint endpoint = (ServiceEndpoint)bridge.target();
		if (endpoint == null)
			return null;

		String serviceDid = endpoint.getServiceInstanceDid();
		String address = endpoint.getProviderAddress();

		String jwtCode = storage.loadAccessTokenByAddress(address);
		if (jwtCode == null && serviceDid != null) {
			jwtCode = storage.loadAccessToken(serviceDid);
		}

		if (jwtCode != null && this.isExpired(jwtCode)) {
			storage.clearAccessTokenByAddress(address);
			storage.clearAccessToken(serviceDid);
		}

		return jwtCode;
	}

	private boolean isExpired(String jwtCode) {
		try {
			Claims claims = new JwtParserBuilder().setAllowedClockSkewSeconds(300).build().parseClaimsJws(jwtCode).getBody();
			return System.currentTimeMillis() > (claims.getExpiration().getTime());
		} catch (Exception e) {
			return true;
		}
	}

	private void saveToken(String jwtCode) {
		ServiceEndpoint endpoint = (ServiceEndpoint)bridge.target();
		if (endpoint == null)
			return;

		storage.storeAccessToken(endpoint.getServiceInstanceDid(), jwtCode);
		storage.storeAccessTokenByAddress(endpoint.getProviderAddress(), jwtCode);
	}

	private void clearToken() {
		ServiceEndpoint endpoint = (ServiceEndpoint)bridge.target();
		if (endpoint == null)
			return;

		storage.clearAccessToken(endpoint.getServiceInstanceDid());
		storage.clearAccessTokenByAddress(endpoint.getProviderAddress());
	}
}
