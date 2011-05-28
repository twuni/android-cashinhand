package org.twuni.money.wallet.exception;

import org.twuni.money.wallet.model.Dollar;

public class WorthlessMoney extends RuntimeException {

	private final Dollar dollar;

	public WorthlessMoney( Dollar dollar ) {
		this.dollar = dollar;
	}

	public Dollar getDollar() {
		return dollar;
	}

	@Override
	public String getMessage() {
		return String.format( "The dollar [%s], previously worth $%.2f, is now worth nothing.", dollar.getId(), Double.valueOf( dollar.getWorth() / 100.0 ) );
	}

}
