package org.elastos.hive;

import java.util.concurrent.CompletableFuture;
import org.elastos.did.DIDDocument;

/**
 *
 */
public interface AppContextProvider {
	String getLocalDataDir();
    DIDDocument getAppInstanceDocument();
    CompletableFuture<String> getAuthorization(String jwtToken);

    /**
     * Used for backup authorization.
     * @param srdDid
     * @param targetDid
     * @param targetHost
     * @return
     */
    CompletableFuture<String> getAuthorization(String srdDid, String targetDid, String targetHost);
}
