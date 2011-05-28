package org.twuni.money.wallet.model;

public class Dollar {

	private String id;
	private String secret;
	private int worth;

	public Dollar() {
	}

	public Dollar( String id, String secret, int worth ) {
		this.id = id;
		this.secret = secret;
		this.worth = worth;
	}

	public String getId() {
		return id;
	}

	public void setId( String id ) {
		this.id = id;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret( String secret ) {
		this.secret = secret;
	}

	public int getWorth() {
		return worth;
	}

	public void setWorth( int worth ) {
		this.worth = worth;
	}

}
