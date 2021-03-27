package org.elastos.hive.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elastos.hive.AppContext;
import org.elastos.hive.AppContextProvider;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.UnauthorizedStateException;
import org.elastos.hive.network.request.SignInRequestBody;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.network.response.SignInResponseBody;

import java.util.HashMap;

public class BackupRemoteResolver implements TokenResolver {
    private AppContextProvider contextProvider;
    private ConnectionManager connectionManager;
    private String targetDid;
    private String targetHost;

    public BackupRemoteResolver(AppContext context, String targetDid, String targetHost) {
        this.contextProvider = context.getAppContextProvider();
        this.connectionManager = context.getConnectionManager();
        this.targetDid = targetDid;
        this.targetHost = targetHost;
    }

    @Override
    public AuthToken getToken() throws HiveException {
        return credential(signIn());
    }

    private AuthToken credential(String sourceDid) {
        try {
            return new AuthToken(contextProvider.getAuthorization(sourceDid, this.targetDid, this.targetHost)
                    .get(), Long.MAX_VALUE, AuthToken.TYPE_BACKUP);
        } catch (Exception e) {
            throw new UnauthorizedStateException("Failed to authentication backup credential.");
        }
    }

    private String signIn() throws HiveException {
        try {
            SignInResponseBody rspBody = connectionManager.getAuthApi()
                    .signIn(new SignInRequestBody(new ObjectMapper()
                            .readValue(contextProvider.getAppInstanceDocument().toString(), HashMap.class)))
                    .execute()
                    .body();
            return HiveResponseBody.validateBody(rspBody)
                    .checkValid(contextProvider.getAppInstanceDocument().getSubject().toString())
                    .getIssuer();
        } catch (Exception e) {
            throw new HiveException(e.getMessage());
        }
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
