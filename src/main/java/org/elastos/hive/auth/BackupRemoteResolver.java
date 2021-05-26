package org.elastos.hive.auth;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.HttpFailedException;
import org.elastos.hive.service.BackupContext;

import java.util.concurrent.ExecutionException;

public class BackupRemoteResolver implements TokenResolver {
    private BackupContext backupContext;
    private String targetDid;
    private String targetHost;
    //private AuthenticationServiceRender authenticationService;

    public BackupRemoteResolver(ServiceEndpoint serviceEndpoint, BackupContext backupContext,
                                String targetServiceDid, String targetAddress) {
        this.backupContext = backupContext;
        this.targetDid = targetServiceDid;
        this.targetHost = targetAddress;
        //this.authenticationService = new AuthenticationServiceRender(serviceEndpoint);
    }

    @Override
    public String getToken() throws HttpFailedException {
       /* try {
            return credential(authenticationService.signIn4ServiceDid());
        } catch (Exception e) {
            throw new HttpFailedException(401, "Failed to authentication backup credential.");
        }*/
    	return null;
    }

    /*
    private AuthToken credential(String sourceDid) throws ExecutionException, InterruptedException {
        return new AuthTokenToBackup(backupContext
                .getAuthorization(sourceDid, this.targetDid, this.targetHost).get(),
                0);
    }*/

    @Override
    public void invalidateToken() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNextResolver(TokenResolver resolver) {
        throw new UnsupportedOperationException();
    }
}
