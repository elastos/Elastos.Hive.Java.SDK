package org.elastos.hive.vault.backup.credential;

import org.elastos.did.VerifiableCredential;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.auth.CodeFetcher;
import org.elastos.hive.service.BackupContext;
import org.elastos.hive.utils.LogUtil;

public class CredentialCode {
	public static final String TOKEN_TYPE = "backup";
	private String jwtCode;
	private CodeFetcher resolver;

	public CredentialCode(ServiceEndpoint endpoint, BackupContext context) {
		CodeFetcher remoteResolver = new RemoteResolver(
        		endpoint,context,
        		context.getParameter("targetServiceDid"),
        		context.getParameter("targetAddress"));
		resolver = new LocalResolver(endpoint, remoteResolver);
	}

	public String getToken() {
		if (jwtCode != null)
			return jwtCode;

		try {
			jwtCode = resolver.fetch();
		} catch (Exception e) {
			// TODO:
			e.printStackTrace();
		}

		return jwtCode;
	}

	public boolean isExpired() {
		try {
			return VerifiableCredential.fromJson(jwtCode).isExpired();
		} catch (Exception e) {
			LogUtil.e("Failed to check backup credential with message:" + e.getMessage());
			return true;
		}
	}
}
