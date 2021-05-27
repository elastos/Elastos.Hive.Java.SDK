package org.elastos.hive.vault.backup.credential;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.auth.CodeResolver;
import org.elastos.hive.exception.HttpFailedException;
import org.elastos.hive.storage.DataStorage;

class LocalResolver implements CodeResolver {
	private ServiceEndpoint serviceEndpoint;
	private CodeResolver nextResolver;
	private DataStorage storage;

    public LocalResolver(ServiceEndpoint serviceEndpoint, CodeResolver next) {
    	this.serviceEndpoint = serviceEndpoint;
    	this.nextResolver = next;
    	this.storage = serviceEndpoint.getAppContext().getDataStorage();
    }

    @Override
	public String resolve() throws HttpFailedException {
		String token = restoreToken();
		if (token == null) {
			token = nextResolver.resolve();
			saveToken(token);
		}

		return token;
	}

	@Override
	public void invalidate() {
		clearToken();
	}

	private String restoreToken() {
        if (serviceEndpoint.getServiceInstanceDid() == null)
            return null;

        return storage.loadBackupCredential(serviceEndpoint.getServiceInstanceDid());
    }

    private void saveToken(String token) {
        if (serviceEndpoint.getServiceInstanceDid() != null)
        	storage.storeBackupCredential(serviceEndpoint.getServiceInstanceDid(), token);
    }

    private void clearToken() {
        if (serviceEndpoint.getServiceInstanceDid() != null)
        	storage.clearBackupCredential(serviceEndpoint.getServiceInstanceDid());
    }
}
