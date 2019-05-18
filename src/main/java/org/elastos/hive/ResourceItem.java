package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface ResourceItem<R extends ResultItem> extends ResultItem {
	public String getId();
	public R getLastInfo();

	public CompletableFuture<R> getInfo();
	public CompletableFuture<R> getInfo(Callback<R> callback);
}
