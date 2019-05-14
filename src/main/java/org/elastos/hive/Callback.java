package org.elastos.hive;

public interface Callback<T extends BaseItem> {
	public void onError(HiveException e);
	public void onSuccess(T object);
}
