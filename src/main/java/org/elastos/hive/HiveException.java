package org.elastos.hive;

public class HiveException extends Exception {
	private static final long serialVersionUID = 1486850840770311509L;

	HiveException(Exception e) {
		super(e);
	}

	HiveException(HiveException e) {
		super(e.getMessage());
	}

	public HiveException(final String message) {
		super(message);
	}
}
