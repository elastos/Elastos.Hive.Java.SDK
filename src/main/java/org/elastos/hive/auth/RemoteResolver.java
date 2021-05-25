package org.elastos.hive.auth;

import com.google.common.base.Throwables;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.elastos.did.jwt.Claims;
import org.elastos.hive.AppContextProvider;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.HttpFailedException;
import org.elastos.hive.utils.JwtUtil;
import org.elastos.hive.utils.LogUtil;

public class RemoteResolver implements TokenResolver {
	private ServiceEndpoint serviceEndpoint;
	private AppContextProvider contextProvider;
	private AuthController controller;

	public RemoteResolver(ServiceEndpoint serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
		this.contextProvider = serviceEndpoint.getAppContext().getAppContextProvider();
		this.controller = new AuthController(serviceEndpoint);
	}

	@Override
	public AuthToken getToken() throws HttpFailedException {
		try {
			String accessToken = controller.auth(signIn4AccessToken());
	        long exp = JwtUtil.getBody(accessToken).getExpiration().getTime();
	        long expiresTime = System.currentTimeMillis() / 1000 + exp / 1000;
	        return new AuthTokenToVault(accessToken, expiresTime);
		} catch (Exception e) {
			LogUtil.d(Throwables.getStackTraceAsString(e));
			throw new HttpFailedException(401, "Failed to get token by auth requests.");
		}
	}

	public String signIn() throws IOException {
        String challenge = controller.signIn(contextProvider.getAppInstanceDocument().getSubject().toString());
        Claims claims = JwtUtil.getBody(challenge);
        // Update the service did to service end-point for future usage.
        serviceEndpoint.setServiceInstanceDid(claims.getIssuer());
        serviceEndpoint.setAppInstanceDid(claims.getSubject());
        return challenge;
    }

	private String signIn4AccessToken() throws IOException, ExecutionException, InterruptedException {
        return contextProvider.getAuthorization(signIn()).get();
    }

	@Override
	public void invalidateToken() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setNextResolver(TokenResolver resolver) {
		throw new UnsupportedOperationException();
	}
}
