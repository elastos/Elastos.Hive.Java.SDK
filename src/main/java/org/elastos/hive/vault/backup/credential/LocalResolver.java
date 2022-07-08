package org.elastos.hive.vault.backup.credential;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.connection.auth.CodeFetcher;
import org.elastos.hive.exception.NotImplementedException;

class LocalResolver implements CodeFetcher {
	private ServiceEndpoint serviceEndpoint;
	private CodeFetcher nextResolver;

	public LocalResolver(ServiceEndpoint serviceEndpoint, CodeFetcher next) {
		this.serviceEndpoint = serviceEndpoint;
		this.nextResolver = next;
	}

	@Override
	public String fetch() throws NodeRPCException {
		return this.nextResolver.fetch();
	}

	@Override
	public void invalidate() {
		throw new NotImplementedException();
	}
}
