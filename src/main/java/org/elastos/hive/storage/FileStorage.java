package org.elastos.hive.storage;

public class FileStorage implements DataStorage {
	@Override
	public String loadBackupCredential(String serviceDid) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String loadSignInCredential() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String loadAccessToken(String serviceDid) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String loadAccessTokenByAddress(String providerAddress) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void storeBackupCredential(String serviceDid, String credential) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void storeSignInCredential(String credential) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void storeAccessToken(String serviceDid, String accessToken) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void storeAccessTokenByAddress(String serviceDid, String providerAddress) {
		throw new UnsupportedOperationException();
	}
}
