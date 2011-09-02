package com.mychoize.android.cricscore.app;

public class VersionNotSupportedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8566081175013460134L;

	public VersionNotSupportedException() {

	}

	public VersionNotSupportedException(String detailMessage) {
		super(detailMessage);
	}

	public VersionNotSupportedException(Throwable throwable) {
		super(throwable);
	}

	public VersionNotSupportedException(String detailMessage,
			Throwable throwable) {
		super(detailMessage, throwable);
	}

}
