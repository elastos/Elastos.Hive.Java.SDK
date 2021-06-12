package org.elastos.hive.vault.backup.credential;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.auth.CodeResolver;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.storage.DataStorage;

class LocalResolver implements CodeResolver {
	private ServiceEndpoint serviceEndpoint;
	private CodeResolver nextResolver;

    public LocalResolver(ServiceEndpoint serviceEndpoint, CodeResolver next) {
    	this.serviceEndpoint = serviceEndpoint;
    	this.nextResolver = next;
    }

    @Override
	public String resolve() throws NodeRPCException {
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
		DataStorage storage = serviceEndpoint.getStorage();

        if (serviceEndpoint.getServiceInstanceDid() == null)
            return null;

        return storage.loadBackupCredential(serviceEndpoint.getServiceInstanceDid());
    }

    private void saveToken(String token) {
    	DataStorage storage = serviceEndpoint.getStorage();

        if (serviceEndpoint.getServiceInstanceDid() != null)
        	storage.storeBackupCredential(serviceEndpoint.getServiceInstanceDid(), token);
    }

    private void clearToken() {
    	DataStorage storage = serviceEndpoint.getStorage();

        if (serviceEndpoint.getServiceInstanceDid() != null)
        	storage.clearBackupCredential(serviceEndpoint.getServiceInstanceDid());
    }
}
