package org.elastos.hive;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public abstract class Directory extends Result implements ResourceItem<Directory.Info>, DirectoryItem,  FileItem {
	public static class Info extends AttributeMap {
		public static final String itemId = "ItemId";
		public static final String name   = "Name";
		public static final String childCount   = "ChildCount";

		public Info(HashMap<String, String> hash) {
			super(hash);
		}
	}

	public abstract CompletableFuture<Children> getChildren();
	public abstract CompletableFuture<Children> getChildren(Callback<Children> callback);
}
