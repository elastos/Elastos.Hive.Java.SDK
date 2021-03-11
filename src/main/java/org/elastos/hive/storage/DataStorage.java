package org.elastos.hive.storage;

public interface DataStorage {
	public String loadBackupCredential(String serviceDid);
	public String loadSigninCredential();
	public String loadAccessToken(String serviceDid);
	public String loadAccessTokenByAddress(String providerAddress);

	public void storeBackupCredential(String serviceDid, String credential);
	public void storeSigninCredential(String credential);
	public void storeAccessToken(String serviceDid, String accessToken);
	public void storeAccessTokenByAddress(String serviceDid, String providerAddress);
}
