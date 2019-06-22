package org.elastos.hive;

import java.util.HashMap;

public abstract class File extends Result implements ResourceItem<File.Info>, FileItem {
	public static class Info extends AttributeMap {
		public static final String itemId = "ItemId";
		public Info(HashMap<String, String> hash) {
			super(hash);
		}
	}
}
