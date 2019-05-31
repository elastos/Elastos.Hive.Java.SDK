package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public abstract class Directory implements ResourceItem<Directory.Info>, DirectoryItem,  FileItem {
	public static class Info implements ResultItem {
		private final String itemId;

		public Info(String itemId) {
			this.itemId = itemId;
		}

		public String getId() {
			return itemId;
		}
	}

	public abstract CompletableFuture<Children> getChildren();
	public abstract CompletableFuture<Children> getChildren(Callback<Children> callback);
}
