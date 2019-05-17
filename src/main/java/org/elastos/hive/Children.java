package org.elastos.hive;

import java.util.ArrayList;

public class Children implements ResultItem {
	private final ArrayList<Object> children;

	public Children(ArrayList<Object> children) {
		this.children = children;
	}

	public ArrayList<Object> getContent() {
		return children;
	}
}
