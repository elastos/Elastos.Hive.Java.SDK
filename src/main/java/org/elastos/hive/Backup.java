package org.elastos.hive;


import org.elastos.hive.backup.State;

import java.util.concurrent.CompletableFuture;

public interface Backup {


	CompletableFuture<State> state();


	CompletableFuture<Boolean> save(String credential);

	CompletableFuture<Boolean> restore(String credential);

	CompletableFuture<Boolean> active();
}


