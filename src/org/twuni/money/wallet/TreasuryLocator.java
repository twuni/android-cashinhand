package org.twuni.money.wallet;

import org.apache.http.client.HttpClient;
import org.twuni.money.bank.model.Dollar;
import org.twuni.money.bank.model.Treasury;
import org.twuni.money.bank.util.Locator;

public class TreasuryLocator implements Locator<Dollar, Treasury> {

	private final HttpClient client;

	public TreasuryLocator( HttpClient client ) {
		this.client = client;
	}

	@Override
	public Treasury lookup( Dollar dollar ) {
		return new Treasury( client, dollar.getTreasury() );
	}

}