package org.elastos.hive;

public class NullCallback<T extends BaseItem> implements Callback<T> {
	@Override
	public void onFailed(HiveException e) {
	}

	@Override
	public void onSuccess(T object) {
	}
}
