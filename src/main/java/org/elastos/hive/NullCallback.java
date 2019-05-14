package org.elastos.hive;

public class NullCallback<T extends BaseItem> implements Callback<T> {
	@Override
	public void onError(HiveException e) {
	}

	@Override
	public void onSuccess(T object) {
	}
}
