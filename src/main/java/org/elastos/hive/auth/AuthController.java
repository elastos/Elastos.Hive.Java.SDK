package org.elastos.hive.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elastos.hive.AppContextProvider;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.HiveResponseBody;

import java.io.IOException;
import java.util.HashMap;

public class AuthController {
    private AppContextProvider provider;
    private AuthAPI authAPI;

    public AuthController(ServiceEndpoint serviceEndpoint) {
        this.provider = serviceEndpoint.getAppContext().getAppContextProvider();
        this.authAPI = serviceEndpoint.getConnectionManager().createService(AuthAPI.class, false);
    }

    public String signIn(String appInstanceDid) throws IOException {
        SignInResponse body = HiveResponseBody.validateBody(authAPI.signIn(new SigninRequest(new ObjectMapper()
                        .readValue(provider.getAppInstanceDocument().toString(), HashMap.class)))
                .execute()
                .body());
        body.checkValid(appInstanceDid);
        return body.getChallenge();
    }

    public String auth(String token) throws IOException {
        return HiveResponseBody.validateBody(authAPI.auth(new AuthRequest(token)).execute().body()).getToken();
    }
}
