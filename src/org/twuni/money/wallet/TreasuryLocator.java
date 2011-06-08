package org.twuni.money.wallet;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.twuni.money.bank.model.Dollar;
import org.twuni.money.bank.model.Treasury;
import org.twuni.money.bank.util.Locator;

public class TreasuryLocator implements Locator<Dollar, Treasury> {

	private final HttpClient client;
	private final Map<String, Treasury> treasuries = new HashMap<String, Treasury>();

	public TreasuryLocator( HttpClient client ) {
		this.client = client;
	}

	@Override
	public Treasury lookup( Dollar dollar ) {

		Treasury treasury = treasuries.get( dollar.getTreasury() );

		if( treasury == null ) {
			treasury = new Treasury( client, dollar.getTreasury() );
			treasuries.put( dollar.getTreasury(), treasury );
		}

		return treasury;

	}

}
