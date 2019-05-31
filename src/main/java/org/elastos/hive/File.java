package org.elastos.hive;

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
}
