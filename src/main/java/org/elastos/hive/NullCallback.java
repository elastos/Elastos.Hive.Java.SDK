package org.elastos.hive;

public class NullCallback<T extends ResultItem> implements Callback<T> {
	@Override
	public void onError(HiveException e) {
	}

	@Override
	public void onSuccess(T object) {
	}
}
