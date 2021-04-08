package org.elastos.hive.auth;

import org.elastos.hive.AppContextProvider;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HttpFailedException;
import org.elastos.hive.service.BackupContext;
import org.elastos.hive.vault.AuthenticationServiceRender;

import java.util.concurrent.ExecutionException;

public class BackupRemoteResolver implements TokenResolver {
    private AppContextProvider contextProvider;
    private BackupContext backupContext;
    private ConnectionManager connectionManager;
    private String targetDid;
    private String targetHost;
    private AuthenticationServiceRender authenticationService;

    public BackupRemoteResolver(ServiceEndpoint serviceEndpoint, BackupContext backupContext,
                                String targetServiceDid, String targetAddress) {
        this.contextProvider = serviceEndpoint.getAppContext().getAppContextProvider();
        this.backupContext = backupContext;
        this.connectionManager = serviceEndpoint.getConnectionManager();
        this.targetDid = targetServiceDid;
        this.targetHost = targetAddress;
        this.authenticationService = new AuthenticationServiceRender(serviceEndpoint);
    }

    @Override
    public AuthToken getToken() throws HttpFailedException {
        try {
            return credential(authenticationService.signIn4Issuer());
        } catch (Exception e) {
            throw new HttpFailedException(401, "Failed to authentication backup credential.");
        }
    }

    private AuthToken credential(String sourceDid) throws ExecutionException, InterruptedException {
        return new AuthToken(backupContext
                .getAuthorization(sourceDid, this.targetDid, this.targetHost).get(),
                0, AuthToken.TYPE_BACKUP);
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
