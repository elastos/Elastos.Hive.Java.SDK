package org.elastos.hive.storage;

public interface DataStorage {
	String loadBackupCredential(String serviceDid);
	String loadSignInCredential();

	/**
	 * Load access token by serviceDid which can be used for authorization.
	 * @param serviceDid service did
	 * @return access token
	 */
	String loadAccessToken(String serviceDid);

	/**
	 * Load access token by provider address which can be used for authorization.
	 * @param providerAddress provider address to access
	 * @return access token
	 */
	String loadAccessTokenByAddress(String providerAddress);

	void storeBackupCredential(String serviceDid, String credential);
	void storeSignInCredential(String credential);

	/**
	 * Store access token to data storage by service did.
	 * @param serviceDid service did
	 * @param accessToken access token to be stored
	 */
	void storeAccessToken(String serviceDid, String accessToken);

	/**
	 * Store access token to data storage by provider address.
	 * @param providerAddress provider address
	 * @param accessToken access token
	 */
	void storeAccessTokenByAddress(String providerAddress, String accessToken);

	void clearBackupCredential(String serviceDid);
	void clearSignInCredential();
	void clearAccessToken(String serviceDid);
	void clearAccessTokenByAddress(String providerAddress);
}
