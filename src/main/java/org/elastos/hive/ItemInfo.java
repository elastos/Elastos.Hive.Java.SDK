/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.hive;

import java.util.HashMap;

/**
 * The `HiveItemInfo` object is a property bag for ClientInfo information.
 */
public class ItemInfo extends AttributeMap {
	/**
	 * The unique identifier of the item within the Drive.
	 */
	public static final String itemId = "ItemId";

	/**
	 * The name of the item (filename and extension)
	 */
	public static final String name   = "Name";

	/**
	 * The item type is `file` or `directory`
	 */
	public static final String type   = "Type";

	/**
	 * Size of the item in bytes
	 */
	public static final String size   = "Size";

	/**
	 * ItemInfo constructor
	 * @param hash The map with the `itemId`, `name`, `type` and `size` key-value
	 */
	public ItemInfo(HashMap<String, String> hash) {
		super(hash);
	}
}
