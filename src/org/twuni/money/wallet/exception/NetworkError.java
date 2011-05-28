package org.twuni.money.wallet.exception;

import java.io.IOException;

public class NetworkError extends RuntimeException {

	public NetworkError( IOException exception ) {
		super( exception );
	}

	@Override
	public String getMessage() {
		return getCause().getMessage();
	}

}
