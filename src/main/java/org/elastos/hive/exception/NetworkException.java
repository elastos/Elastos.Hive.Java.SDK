package org.elastos.hive.exception;

public class NetworkException extends HiveException {
	private static final long serialVersionUID = 4875908215630582817L;

	public NetworkException(String message) {
		super(String.format("Unkown network exception with message: %s", message));
	}
}
