package org.elastos.hive;

import java.util.ArrayList;

public class Children extends Result {
	private final ArrayList<ItemInfo> children;

	public Children(ArrayList<ItemInfo> children) {
		this.children = children;
	}

	public ArrayList<ItemInfo> getContent() {
		return children;
	}
}
