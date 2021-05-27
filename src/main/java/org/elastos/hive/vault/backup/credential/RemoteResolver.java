package org.elastos.hive.vault.backup.credential;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.auth.CodeResolver;
import org.elastos.hive.exception.HttpFailedException;
import org.elastos.hive.service.BackupContext;

public class RemoteResolver implements CodeResolver {
    private BackupContext backupContext;
    private String targetDid;
    private String targetHost;
    //private AuthenticationServiceRender authenticationService;

    public RemoteResolver(ServiceEndpoint serviceEndpoint, BackupContext backupContext,
                          String targetServiceDid, String targetAddress) {
        this.backupContext = backupContext;
        this.targetDid = targetServiceDid;
        this.targetHost = targetAddress;
    }

    @Override
    public String resolve() throws HttpFailedException {
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
    public void invalidate() {}
}
