package org.elastos.hive;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;

public abstract class File implements ResourceItem<File.Info>, FileItem {
	public static class Info implements ResultItem {
		private final String fileId;

		public Info(String fileId) {
			this.fileId = fileId;
		}

		public String getId() {
			return fileId;
		}
	}

	public abstract CompletableFuture<Status> read(OutputStream outputStream);
	public abstract CompletableFuture<Status> read(OutputStream outputStream, Callback<Status> callback);

	public abstract CompletableFuture<Status> write(InputStream inputStream);
	public abstract CompletableFuture<Status> write(InputStream inputStream, Callback<Status> callback);
}
