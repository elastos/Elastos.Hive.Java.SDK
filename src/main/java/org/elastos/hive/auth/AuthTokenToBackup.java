package org.elastos.hive.auth;

import org.elastos.did.VerifiableCredential;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.service.BackupContext;
import org.elastos.hive.utils.LogUtil;

public class AuthTokenToBackup extends AuthToken {
	public static final String TOKEN_TYPE = "backup";
	private String jwtCode;
	private TokenResolver resolver;

	public AuthTokenToBackup(ServiceEndpoint endpoint, BackupContext context) {
		TokenResolver remoteResolver = new BackupRemoteResolver(
        		endpoint,
        		context,
        		context.getParameter("targetServiceDid"),
        		context.getParameter("targetAddress"));
		resolver = new LocalResolver(endpoint);
		resolver.setNextResolver(remoteResolver);
	}

	public String getToken() {
		if (jwtCode != null)
			return jwtCode;

		try {
			jwtCode = resolver.getToken();
		} catch (Exception e) {
			// TODO:
			e.printStackTrace();
		}

		return jwtCode;
	}

	@Override
	public String getCanonicalizedAccessToken() {
		return TOKEN_TYPE + " " + jwtCode;
	}

	@Override
	public boolean isExpired() {
		try {
			return VerifiableCredential.fromJson(jwtCode).isExpired();
		} catch (Exception e) {
			LogUtil.e("Failed to check backup credential with message:" + e.getMessage());
			return true;
		}
	}
}
