package org.elastos.hive;

import java.util.HashMap;

public class ItemInfo extends AttributeMap {
	public static final String itemId = "ItemId";
	public static final String name   = "Name";
	public static final String type   = "Type";
	public static final String size   = "Size";

	public ItemInfo(HashMap<String, String> hash) {
		super(hash);
	}
}
