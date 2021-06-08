package org.elastos.hive.exception;

public class UnknownServerException extends HiveException {
	private static final long serialVersionUID = 5210865275817148567L;

	public UnknownServerException(NodeRPCException e) {
		this(e.getCode(), e.getMessage());
	}

	public UnknownServerException(String message) {
		super(String.format("Unkown IO exception with message: %s", message));
	}

	public UnknownServerException(int httpCode, String message) {
		super(String.format("Impossible Exception with HTTP code:%d and message: %s", httpCode, message));
	}
}
