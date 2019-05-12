package org.elastos.hive;

public class Result<T extends HiveItem> {
	private final HiveException exception;
	private final T object;

	public Result(T object) {
		this.object = object;
		this.exception = null;
	}

	public Result(HiveException e) {
		this.object = null;
		this.exception = e;
	}

	public T getObject()  {
		return object;
	}

	public HiveException getException() {
		return exception;
	}

	public boolean isFailed() {
		return exception != null;
	}
}
