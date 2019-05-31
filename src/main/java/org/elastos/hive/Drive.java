package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public abstract class Drive implements ResourceItem<Drive.Info>, DirectoryItem {
	public static class Info implements ResultItem {
		private final String driveId;

		public Info(String driveId) {
			this.driveId = driveId;
		}

		public String getId() {
			return driveId;
		}
	}

	public abstract DriveType getType();

	public abstract CompletableFuture<Directory> getRootDir();
	public abstract CompletableFuture<Directory> getRootDir(Callback<Directory> callback);
}
