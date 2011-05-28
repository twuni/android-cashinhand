package org.twuni.money.wallet.exception;

public class InsufficientFunds extends RuntimeException {

	public InsufficientFunds() {
		super( "Insufficient funds available to complete this transaction." );
	}

}
