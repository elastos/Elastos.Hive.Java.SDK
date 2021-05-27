package org.elastos.hive.auth;

import com.google.common.base.Throwables;

import org.elastos.did.jwt.Claims;
import org.elastos.hive.AppContextProvider;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.auth.controller.AuthController;
import org.elastos.hive.exception.HttpFailedException;
import org.elastos.hive.utils.JwtUtil;
import org.elastos.hive.utils.LogUtil;

public class RemoteResolver implements CodeResolver {
	private ServiceEndpoint serviceEndpoint;
	private AppContextProvider contextProvider;
	private AuthController controller;

	public RemoteResolver(ServiceEndpoint serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
		this.contextProvider = serviceEndpoint.getAppContext().getAppContextProvider();
		this.controller = new AuthController(serviceEndpoint);
	}

	@Override
	public String resolve() throws HttpFailedException {
		try {
			String challenge = controller.signIn(contextProvider.getAppInstanceDocument().toString());
			Claims claims = JwtUtil.getBody(challenge);
	        // Update the service did to service end-point for future usage.
	        serviceEndpoint.setServiceInstanceDid(claims.getIssuer());
	        serviceEndpoint.setAppInstanceDid(claims.getSubject());

	        String challengeResponse = contextProvider.getAuthorization(challenge).get();
	        return controller.auth(challengeResponse);
		} catch (Exception e) {
			// TODO:
			e.printStackTrace();
			LogUtil.d(Throwables.getStackTraceAsString(e));
			throw new HttpFailedException(401, "Failed to get token by auth requests.");
		}
	}

	@Override
	public void invalidate() {}
}
