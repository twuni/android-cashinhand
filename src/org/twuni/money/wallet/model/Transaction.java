package org.twuni.money.wallet.model;

import android.location.Location;

public class Transaction {

	private int amount;
	private Location location;
	private String notes;
	private long timestamp;

	public int getAmount() {
		return amount;
	}

	public Location getLocation() {
		return location;
	}

	public String getNotes() {
		return notes;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setAmount( int amount ) {
		this.amount = amount;
	}

	public void setLocation( Location location ) {
		this.location = location;
	}

	public void setNotes( String notes ) {
		this.notes = notes;
	}

	public void setTimestamp( long timestamp ) {
		this.timestamp = timestamp;
	}

}
