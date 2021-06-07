package org.elastos.hive.exception;

public class UnknownException extends HiveException {
	private static final long serialVersionUID = 5210865275817148567L;

	public UnknownException(RPCException e) {
		this(e.getCode(), e.getMessage());
	}

	public UnknownException(String message) {
		super(String.format("Unkown IO exception with message: %s", message));
	}

	public UnknownException(int httpCode, String message) {
		super(String.format("Impossible Exception with HTTP code:%d and message: %s", httpCode, message));
	}
}
