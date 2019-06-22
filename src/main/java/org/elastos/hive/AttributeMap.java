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
