package org.elastos.hive.exceptions;


public class ErrorResponseException extends Exception {
	private static final long serialVersionUID = -1799958534485231334L;
	
	private final int expectedResponse;
	private final int givenResponse;
	private final String errorCode;
	private final String errorMessage;

	public ErrorResponseException(int expectedResponse, int givenResponse, String errorCode, String errorMessage) {
		//super(String.format("Expected %d response code, but received %d. It means %s (%s).",
		// expectedResponse, givenResponse, errorCode, errorMessage));
		this.expectedResponse = expectedResponse;
		this.givenResponse = givenResponse;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
}
