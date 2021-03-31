package org.elastos.hive.vault;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elastos.hive.AppContext;
import org.elastos.hive.AppContextProvider;
import org.elastos.hive.auth.AuthToken;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.network.request.AuthRequestBody;
import org.elastos.hive.network.request.SignInRequestBody;
import org.elastos.hive.network.response.AuthResponseBody;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.network.response.SignInResponseBody;
import org.elastos.hive.utils.JwtUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class AuthenticationServiceRender extends HiveVaultRender implements HttpExceptionHandler {

    private AppContextProvider contextProvider;

    public AuthenticationServiceRender(AppContext context, AppContextProvider contextProvider,
                                       ConnectionManager connectionManager) {
        super(context, connectionManager);
        this.contextProvider = contextProvider;
    }

    public String signIn4Token() throws IOException, ExecutionException, InterruptedException {
        SignInResponseBody rspBody = HiveResponseBody.validateBody(
                getConnectionManager().getAuthApi()
                        .signIn(new SignInRequestBody(new ObjectMapper()
                                .readValue(
                                        contextProvider.getAppInstanceDocument().toString(),
                                        HashMap.class)))
                        .execute()
                        .body());
        rspBody.checkValid(contextProvider.getAppInstanceDocument().getSubject().toString());
        return contextProvider.getAuthorization(rspBody.getChallenge()).get();
    }

    public String signIn4Issuer() throws IOException {
        SignInResponseBody rspBody = getConnectionManager().getAuthApi()
                .signIn(new SignInRequestBody(new ObjectMapper()
                        .readValue(contextProvider.getAppInstanceDocument().toString(), HashMap.class)))
                .execute()
                .body();
        return HiveResponseBody.validateBody(rspBody)
                .checkValid(contextProvider.getAppInstanceDocument().getSubject().toString())
                .getIssuer();
    }

    public AuthToken auth(String token) throws IOException {
        AuthResponseBody rspBody = HiveResponseBody.validateBody(
                getConnectionManager().getAuthApi()
                        .auth(new AuthRequestBody(token))
                        .execute()
                        .body());
        long exp = JwtUtil.getBody(rspBody.getToken()).getExpiration().getTime();
        long expiresTime = System.currentTimeMillis() / 1000 + exp / 1000;
        return new AuthToken(rspBody.getToken(), expiresTime, AuthToken.TYPE_TOKEN);
    }
}
