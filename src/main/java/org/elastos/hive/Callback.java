package org.elastos.hive;

public interface Callback<T extends ResultItem> {
	public void onError(HiveException e);
	public void onSuccess(T body);
}
