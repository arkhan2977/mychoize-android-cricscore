package com.mychoize.android.cricscore.app;

public class DataNotFormedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2552890462975168307L;

	public DataNotFormedException() {
	}

	public DataNotFormedException(String detailMessage) {
		super(detailMessage);
	}

	public DataNotFormedException(Throwable throwable) {
		super(throwable);
	}

	public DataNotFormedException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
