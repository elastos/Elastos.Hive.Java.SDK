package org.elastos.hive.storage;

public interface DataStorage {
	String loadBackupCredential(String serviceDid);
	String loadSignInCredential();
	String loadAccessToken(String serviceDid);
	String loadAccessTokenByAddress(String providerAddress);

	void storeBackupCredential(String serviceDid, String credential);
	void storeSignInCredential(String credential);
	void storeAccessToken(String serviceDid, String accessToken);
	void storeAccessTokenByAddress(String serviceDid, String providerAddress);
}
