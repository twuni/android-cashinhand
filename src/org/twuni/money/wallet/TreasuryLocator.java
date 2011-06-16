package org.twuni.money.wallet;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.twuni.money.common.Locator;
import org.twuni.money.common.Token;
import org.twuni.money.common.Treasury;
import org.twuni.money.common.TreasuryClient;

public class TreasuryLocator implements Locator<Token, Treasury> {

	private final HttpClient client;
	private final Map<String, Treasury> treasuries = new HashMap<String, Treasury>();

	public TreasuryLocator( HttpClient client ) {
		this.client = client;
	}

	@Override
	public Treasury lookup( Token token ) {

		Treasury treasury = treasuries.get( token.getTreasury() );

		if( treasury == null ) {
			treasury = new TreasuryClient( client, token.getTreasury() );
			treasuries.put( token.getTreasury(), treasury );
		}

		return treasury;

	}

}
