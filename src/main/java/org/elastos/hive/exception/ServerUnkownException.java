package org.elastos.hive.exception;

import org.elastos.hive.connection.NodeRPCException;

public class ServerUnkownException extends HiveException {
	private static final long serialVersionUID = 5210865275817148567L;

	public ServerUnkownException(String message) {
		super(String.format("Unkown IO exception with message: %s", message));
	}

	public ServerUnkownException(int httpCode, String message) {
		super(String.format("Impossible Exception with HTTP code:%d and message: %s", httpCode, message));
	}

	public ServerUnkownException(NodeRPCException e) {
		this(e.getCode(), e.getMessage());
	}
}