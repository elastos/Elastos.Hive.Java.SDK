package org.elastos.hive;

public interface Callback<T extends HiveItem> {
	public void onFailed(HiveException e);
	public void onSuccess(T object);
}
