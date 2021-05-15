package org.elastos.hive.vault;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elastos.did.jwt.Claims;
import org.elastos.hive.AppContextProvider;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.auth.AuthToken;
import org.elastos.hive.auth.AuthTokenToVault;
import org.elastos.hive.network.request.AuthRequestBody;
import org.elastos.hive.network.request.SignInRequestBody;
import org.elastos.hive.network.response.AuthResponseBody;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.network.response.SignInResponseBody;
import org.elastos.hive.utils.JwtUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class AuthenticationServiceRender extends BaseServiceRender implements ExceptionConvertor {

    private AppContextProvider contextProvider;

    public AuthenticationServiceRender(ServiceEndpoint serviceEndpoint) {
        super(serviceEndpoint);
        this.contextProvider = serviceEndpoint.getAppContext().getAppContextProvider();
    }

    public String signIn4AccessToken() throws IOException, ExecutionException, InterruptedException {
        return contextProvider.getAuthorization(signIn()).get();
    }

    public String signIn4ServiceDid() throws IOException {
        return JwtUtil.getBody(signIn()).getIssuer();
    }

    public String signIn() throws IOException {
        SignInResponseBody rspBody = getConnectionManager().getAuthAPI()
                .signIn(new SignInRequestBody(new ObjectMapper()
                        .readValue(contextProvider.getAppInstanceDocument().toString(), HashMap.class)))
                .execute()
                .body();
        Claims claims = HiveResponseBody.validateBody(rspBody)
                .checkValid(contextProvider.getAppInstanceDocument().getSubject().toString());
        // Update the service did to service end-point for future usage.
        getServiceEndpoint().setServiceDid(claims.getIssuer());
        return rspBody.getChallenge();
    }

    public AuthToken auth(String token) throws IOException {
        AuthResponseBody rspBody = HiveResponseBody.validateBody(
                getConnectionManager().getAuthAPI()
                        .auth(new AuthRequestBody(token))
                        .execute()
                        .body());
        long exp = JwtUtil.getBody(rspBody.getToken()).getExpiration().getTime();
        long expiresTime = System.currentTimeMillis() / 1000 + exp / 1000;
        return new AuthTokenToVault(rspBody.getToken(), expiresTime);
    }
}
