package org.twuni.money.wallet.exception;

import java.util.List;

public class ManyExceptions extends RuntimeException {

	private final List<Exception> exceptions;

	public ManyExceptions( List<Exception> exceptions ) {
		this.exceptions = exceptions;
	}

	@Override
	public String getMessage() {
		return exceptions == null ? "" : exceptions.toString();
	}

	public List<Exception> getExceptions() {
		return exceptions;
	}

}
