package org.elastos.hive.service;

import org.elastos.hive.network.model.Condition;
import org.elastos.hive.network.model.Executable;

import java.util.concurrent.CompletableFuture;

public interface ScriptingService {
    /**
     * Lets the vault owner register a script on his vault for a given app. The script is built on the client side, then
     * serialized and stored on the hive back-end. Later on, anyone, including the vault owner or external users, can
     * use Scripting.call() to execute one of those scripts and get results/data.
     */
    CompletableFuture<Boolean> registerScript(String name, Executable executable, boolean allowAnonymousUser, boolean allowAnonymousApp);

    CompletableFuture<Boolean> registerScript(String name, Condition condition, Executable executable, boolean allowAnonymousUser, boolean allowAnonymousApp);
}
