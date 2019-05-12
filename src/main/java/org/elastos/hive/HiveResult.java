package org.elastos.hive;

public class HiveResult<T> {
	private final HiveException exception;
	private final T object;

	public HiveResult(T object) {
		this.object = object;
		this.exception = null;
	}

	public HiveResult(HiveException e) {
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
