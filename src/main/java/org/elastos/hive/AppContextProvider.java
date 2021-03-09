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
}
