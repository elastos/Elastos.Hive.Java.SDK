package org.elastos.hive.connection.auth;

import org.elastos.did.jwt.*;
import org.elastos.hive.AppContext;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.DataStorage;
import org.elastos.hive.connection.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The access token is made by hive node and represents the user DID and the application DID.
 *
 * <p>Some of the node APIs requires access token when handling request.</p>
 */
public class AccessToken implements CodeFetcher {
	private static final Logger log = LoggerFactory.getLogger(AccessToken.class);

	private ServiceEndpoint endpoint;
	private String jwtCode;
	private CodeFetcher remoteFetcher;
	private DataStorage storage;
	private BridgeHandler bridge;
	private String storageKey;

	/**
	 * Create the access token by service end point, data storage, and bridge handler.
	 *
	 * @param endpoint The service end point.
	 * @param storage The data storage which is used to save the access token.
	 * @param bridge The bridge handle is used for caller to do sth when getting the access token.
	 */
	public AccessToken(ServiceEndpoint endpoint, DataStorage storage, BridgeHandler bridge) {
		this.endpoint = endpoint;
		this.remoteFetcher = new RemoteFetcher(endpoint);
		this.storage = storage;
		this.bridge = bridge;
		this.storageKey = null;
	}

	private String getStorageKey() {
		if (this.storageKey == null) {
			String userDid = this.endpoint.getUserDid();
			String appDid = this.endpoint.getAppDid();
			String hiveUrl = this.endpoint.getProviderAddress();
			this.storageKey = SHA256.generate(userDid + ";" + appDid + ";" + hiveUrl);
		}
		return this.storageKey;
	}

	@Override
	public String fetch() throws NodeRPCException {
		if (jwtCode != null)
			return jwtCode;

		synchronized (AppContext.class) {
			jwtCode = restoreToken();
			if (jwtCode != null) {
				bridge.flush(jwtCode);
				return jwtCode;
			}

			jwtCode = this.fetchFromRemote();
			return jwtCode;
		}
	}

	private String fetchFromRemote() throws NodeRPCException {
		String token = this.remoteFetcher.fetch();
		bridge.flush(token);
		saveToken(token);
		return token;
	}

	@Override
	public void invalidate() {
		clearToken();
	}

	private String restoreToken() {
		String key = this.getStorageKey();
		jwtCode = storage.loadAccessToken(key);

		if (jwtCode != null && this.isExpired(jwtCode)) {
			storage.clearAccessToken(key);
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
		String key = this.getStorageKey();
		storage.storeAccessToken(key, jwtCode);
	}

	private void clearToken() {
		String key = this.getStorageKey();
		storage.clearAccessToken(key);
	}
}
