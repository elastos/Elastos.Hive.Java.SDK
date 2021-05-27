package org.elastos.hive.auth;

import org.elastos.did.VerifiableCredential;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.service.BackupContext;
import org.elastos.hive.utils.LogUtil;

public class BackupCredential {
	public static final String TOKEN_TYPE = "backup";
	private String jwtCode;
	private CodeResolver resolver;

	public BackupCredential(ServiceEndpoint endpoint, BackupContext context) {
		CodeResolver remoteResolver = new BackupRemoteResolver(
        		endpoint,
        		context,
        		context.getParameter("targetServiceDid"),
        		context.getParameter("targetAddress"));
		resolver = new AccessTokenLocalResolver(endpoint, remoteResolver);
	}

	public String getToken() {
		if (jwtCode != null)
			return jwtCode;

		try {
			jwtCode = resolver.resolve();
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
