package org.elastos.hive;

public class NullCallback<T extends Result> implements Callback<T> {
	@Override
	public void onError(HiveException e) {
	}

	@Override
	public void onSuccess(T object) {
	}
}
