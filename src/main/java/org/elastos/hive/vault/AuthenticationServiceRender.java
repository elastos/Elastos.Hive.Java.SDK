package org.elastos.hive.vault;

import org.elastos.did.jwt.Claims;
import org.elastos.hive.AppContextProvider;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.auth.AuthController;
import org.elastos.hive.auth.AuthToken;
import org.elastos.hive.auth.AuthTokenToVault;
import org.elastos.hive.utils.JwtUtil;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class AuthenticationServiceRender implements ExceptionConvertor {

    private AppContextProvider contextProvider;
    private ServiceEndpoint serviceEndpoint;
    private AuthController controller;

    public AuthenticationServiceRender(ServiceEndpoint serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
        this.contextProvider = serviceEndpoint.getAppContext().getAppContextProvider();
        this.controller = new AuthController(serviceEndpoint);
    }

    public String signIn4AccessToken() throws IOException, ExecutionException, InterruptedException {
        return contextProvider.getAuthorization(signIn()).get();
    }


    public String signIn4ServiceDid() throws IOException {
        return JwtUtil.getBody(signIn()).getIssuer();
    }

    public String signIn() throws IOException {
        String challenge = controller.signIn(contextProvider.getAppInstanceDocument().getSubject().toString());
        Claims claims = JwtUtil.getBody(challenge);
        // Update the service did to service end-point for future usage.
        serviceEndpoint.setServiceInstanceDid(claims.getIssuer());
        serviceEndpoint.setAppInstanceDid(claims.getSubject());
        return challenge;
    }

    public AuthToken auth(String token) throws IOException {
        String accessToken = controller.auth(token);
        long exp = JwtUtil.getBody(accessToken).getExpiration().getTime();
        long expiresTime = System.currentTimeMillis() / 1000 + exp / 1000;
        return new AuthTokenToVault(accessToken, expiresTime);
    }
}
