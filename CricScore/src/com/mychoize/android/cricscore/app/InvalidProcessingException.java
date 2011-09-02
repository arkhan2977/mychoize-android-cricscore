package com.mychoize.android.cricscore.app;

public class InvalidProcessingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9073338980221845964L;

	public InvalidProcessingException() {
	}

	public InvalidProcessingException(String detailMessage) {
		super(detailMessage);
	}

	public InvalidProcessingException(Throwable throwable) {
		super(throwable);
	}

	public InvalidProcessingException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
