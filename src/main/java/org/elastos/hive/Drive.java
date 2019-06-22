package org.elastos.hive;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public abstract class Drive extends Result implements ResourceItem<Drive.Info>, DirectoryItem {
	public static class Info extends AttributeMap {
		public static final String driveId = "DriveId";
		public Info(HashMap<String, String> hash) {
			super(hash);
		}
	}

	public abstract DriveType getType();

	public abstract CompletableFuture<Directory> getRootDir();
	public abstract CompletableFuture<Directory> getRootDir(Callback<Directory> callback);
}
