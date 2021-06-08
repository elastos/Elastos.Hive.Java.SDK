package org.elastos.hive.exception;

public class PathNotExistException extends EntityNotFoundException {
	private static final long serialVersionUID = 5181597396226755904L;

	public PathNotExistException() {
        super();
    }

    public PathNotExistException(String message) {
        super(message);
    }

    public PathNotExistException(RPCException e) {
        super(e.getMessage());
    }
}
