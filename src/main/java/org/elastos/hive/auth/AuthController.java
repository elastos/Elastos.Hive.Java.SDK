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
        try {
        	SigninRequest request = new SigninRequest(new ObjectMapper().readValue(provider.getAppInstanceDocument().toString(), HashMap.class));
			return authAPI.signIn(request).execute().body().getChallenge();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }

    public String auth(String token) throws IOException {
    	try {
    		return authAPI.auth(new ChallengeResponse(token)).execute().body().getToken();
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
}
