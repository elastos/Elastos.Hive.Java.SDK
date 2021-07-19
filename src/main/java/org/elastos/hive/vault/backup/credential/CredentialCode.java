package org.elastos.hive.vault.backup.credential;

import org.elastos.hive.DataStorage;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.connection.auth.CodeFetcher;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.BackupContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The credential code is used for the backup of the vault data.
 */
public class CredentialCode {
	private static final Logger log = LoggerFactory.getLogger(CredentialCode.class);
	private String targetServiceDid;
	private String jwtCode;
	private CodeFetcher remoteResolver;
	private DataStorage storage;

	/**
	 * Create the credential code by service end point and the backup context.
	 *
	 * @param endpoint The service end point.
	 * @param context The backup context.
	 */
	public CredentialCode(ServiceEndpoint endpoint, BackupContext context) {
		targetServiceDid = context.getParameter("targetServiceDid");
		CodeFetcher remoteResolver = new RemoteResolver(
				endpoint, context, targetServiceDid, context.getParameter("targetAddress"));
		this.remoteResolver = new LocalResolver(endpoint, remoteResolver);
		storage = endpoint.getStorage();
	}

	/**
	 * Get the token of the credential code.
	 *
	 * @return The token of the credential code.
	 * @throws HiveException The error comes from the hive node.
	 */
	public String getToken() throws HiveException {
		if (jwtCode != null)
			return jwtCode;

		jwtCode = restoreToken();
		if (jwtCode == null) {
			try {
				jwtCode = remoteResolver.fetch();
			} catch (NodeRPCException e) {
				throw new HiveException(e.getMessage());
			}

			if (jwtCode != null) {
				saveToken(jwtCode);
			}
		}
		return jwtCode;
	}

	private String restoreToken() {
		return storage.loadBackupCredential(targetServiceDid);
	}

	private void saveToken(String jwtCode) {
		storage.storeBackupCredential(targetServiceDid, jwtCode);
	}
}
