package org.elastos.hive.storage;

public class FileStorage implements DataStorage {
	@Override
	public String loadBackupCredential(String serviceDid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String loadSignInCredential() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String loadAccessToken(String serviceDid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String loadAccessTokenByAddress(String providerAddress) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeBackupCredential(String serviceDid, String credential) {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeSignInCredential(String credential) {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeAccessToken(String serviceDid, String accessToken) {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeAccessTokenByAddress(String serviceDid, String providerAddress) {
		// TODO Auto-generated method stub
	}
}
