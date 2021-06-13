package org.elastos.hive.exception;

import org.elastos.hive.connection.NodeRPCException;

public class ServerUnkownException extends HiveException {
	private static final long serialVersionUID = 5210865275817148567L;

	public ServerUnkownException() {
		super("Impossible failure happened");
	}

	public ServerUnkownException(String message) {
		super(message);
	}

	public ServerUnkownException(int httpCode, String message) {
		super(String.format("Exception (http code: %d, message: %s)", httpCode, message));
	}

	public ServerUnkownException(NodeRPCException e) {
		this(e.getCode(), e.getMessage());
	}
}