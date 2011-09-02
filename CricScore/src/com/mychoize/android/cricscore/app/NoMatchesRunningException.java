package com.mychoize.android.cricscore.app;

public class NoMatchesRunningException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 799055805180715067L;

	public NoMatchesRunningException() {
	}

	public NoMatchesRunningException(String detailMessage) {
		super(detailMessage);
	}

	public NoMatchesRunningException(Throwable throwable) {
		super(throwable);
	}

	public NoMatchesRunningException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
