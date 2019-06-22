package org.elastos.hive;

public interface Callback<T extends Result> {
	void onError(HiveException e);
	void onSuccess(T body);
}
