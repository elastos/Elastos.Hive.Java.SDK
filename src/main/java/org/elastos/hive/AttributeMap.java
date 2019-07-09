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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class AttributeMap extends Result implements Map<String, String> {
	private final HashMap<String, String> attrHash;

	protected AttributeMap(HashMap<String, String> hash) {
		this.attrHash = hash;
	}

	@Override
	public int size() {
		return attrHash.size();
	}

	@Override
	public boolean isEmpty() {
		return attrHash.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return attrHash.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return attrHash.containsKey(value);
	}

	@Override
	public String get(Object key) {
		return attrHash.get(key);
	}

	@Override
	public String put(String key, String value) {
		return attrHash.put(key, value);
	}

	@Override
	public String remove(Object key) {
		return attrHash.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
		attrHash.putAll(m);
	}

	@Override
	public void clear() {
		attrHash.clear();
	}

	@Override
	public Set<String> keySet() {
		return attrHash.keySet();
	}

	@Override
	public Collection<String> values() {
		return attrHash.values();
	}

	@Override
	public Set<Entry<String, String>> entrySet() {
		return attrHash.entrySet();
	}
}
