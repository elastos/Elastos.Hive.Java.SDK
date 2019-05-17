package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface ResourceItem<R extends ResultItem> extends ResultItem {
	public String getId();
	public R getLastInfo();

	public CompletableFuture<Result<R>> getInfo();
	public CompletableFuture<Result<R>> getInfo(Callback<R> callback);
}
