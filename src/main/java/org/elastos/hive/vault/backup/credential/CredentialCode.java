package org.elastos.hive.vault.backup.credential;

import org.elastos.did.VerifiableCredential;
import org.elastos.did.exception.MalformedCredentialException;
import org.elastos.hive.DataStorage;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.connection.SHA256;
import org.elastos.hive.connection.auth.CodeFetcher;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.UnauthorizedException;
import org.elastos.hive.service.BackupContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The credential code is used for the backup of the vault data.
 */
public class CredentialCode {
	private static final Logger log = LoggerFactory.getLogger(CredentialCode.class);

	private ServiceEndpoint endpoint;
	private String targetServiceDid;
	private String jwtCode;
	private CodeFetcher remoteResolver;
	private DataStorage storage;
	private String storageKay;

	/**
	 * Create the credential code by service end point and the backup context.
	 *
	 * @param endpoint The service end point.
	 * @param context The backup context.
	 */
	public CredentialCode(ServiceEndpoint endpoint, BackupContext context) {
		this.endpoint = endpoint;
		targetServiceDid = context.getParameter("targetServiceDid");
		CodeFetcher remoteResolver = new RemoteResolver(
				endpoint, context, targetServiceDid, context.getParameter("targetAddress"));
		this.remoteResolver = new LocalResolver(endpoint, remoteResolver);
		storage = endpoint.getStorage();
		this.storageKay = null;
	}

	private String getStorageKey() {
		if (this.storageKay == null) {
			String userDid = this.endpoint.getUserDid();
			String sourceDid = this.endpoint.getServiceInstanceDid();
			this.storageKay = SHA256.generate(userDid + ";" + sourceDid + ";" + targetServiceDid);
		}
		return this.storageKay;
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

		if (this.endpoint.getServiceInstanceDid() == null) {
			try {
				this.endpoint.refreshAccessToken();
			} catch (NodeRPCException e) {
				throw new UnauthorizedException(e.getMessage());
			}
		}

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
		String key = this.getStorageKey();
		String cred = storage.loadBackupCredential(key);
		if (cred != null && this.isExpired(cred)) {
			storage.clearBackupCredential(key);
		}
		return cred;
	}

	private boolean isExpired(String credentialStr) {
		try {
			VerifiableCredential c = VerifiableCredential.parse(credentialStr);
			return System.currentTimeMillis() > (c.getExpirationDate().getTime());
		} catch (MalformedCredentialException e) {
			return true;
		}
	}

	private void saveToken(String jwtCode) {
		String key = this.getStorageKey();
		storage.storeBackupCredential(key, jwtCode);
	}
}
