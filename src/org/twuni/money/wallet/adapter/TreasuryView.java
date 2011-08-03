package org.twuni.money.wallet.adapter;

public class TreasuryView {

	private final String url;
	private final int balance;

	public TreasuryView( String url, int balance ) {
		this.url = url;
		this.balance = balance;
	}

	public String getUrl() {
		return url;
	}

	public int getBalance() {
		return balance;
	}

}
