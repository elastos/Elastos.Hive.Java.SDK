package org.elastos.hive;

import java.util.ArrayList;

public class Children implements BaseItem {
	private final String ID = "DirectoryList";

	private ArrayList<Object> children;

	public Children(ArrayList<Object> children) {
		this.children = children;
	}

	@Override
	public String getId() {
		return ID;
	}

	public ArrayList<Object> getContent() {
		return children;
	}
}
