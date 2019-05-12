package org.elastos.hive;

@SuppressWarnings("hiding")
public interface HiveCallback<T, HiveException> {
	public void onFailed(HiveException e);
	public void onSuccess(T object);
}
