package org.elastos.hive;

public interface Callback<T extends ResultItem> {
	void onError(HiveException e);
	void onSuccess(T body);
}
