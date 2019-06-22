package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface ResourceItem<R extends Result> {
	String getId();
	R getLastInfo();

	CompletableFuture<R> getInfo();
	CompletableFuture<R> getInfo(Callback<R> callback);
}
