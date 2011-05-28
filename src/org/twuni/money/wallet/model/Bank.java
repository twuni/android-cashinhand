package org.twuni.money.wallet.model;

import java.util.ArrayList;
import java.util.List;

import org.twuni.money.wallet.exception.InsufficientFunds;
import org.twuni.money.wallet.exception.ManyExceptions;
import org.twuni.money.wallet.exception.NetworkError;
import org.twuni.money.wallet.exception.WorthlessMoney;

public class Bank {

	private final List<Dollar> dollars;
	private final Vault dollarRepository;

	public Bank( Vault dollarRepository ) {
		this.dollarRepository = dollarRepository;
		this.dollars = dollarRepository.load();
	}

	public int getBalance() {
		int balance = 0;
		for( Dollar dollar : dollars ) {
			balance += dollar.getWorth();
		}
		return balance;
	}

	public Dollar withdraw( int amount ) {

		if( dollars.isEmpty() ) {
			throw new InsufficientFunds();
		}

		Dollar existing = dollars.get( 0 );
		Treasury treasury = Treasury.getTreasury( existing );

		try {
			return getDollarForAmount( treasury, existing, amount );
		} catch( IllegalArgumentException exception ) {
			if( dollars.size() < 2 ) {
				throw new InsufficientFunds();
			}
			consolidate();
			return withdraw( amount );
		}

	}

	public void deposit( Dollar dollar ) {

		if( dollars.isEmpty() ) {
			if( dollar.getWorth() > 1 ) {
				Treasury treasury = Treasury.getTreasury( dollar );
				List<Dollar> split = treasury.split( dollar, 1 );
				dollar = treasury.merge( split.get( 0 ), split.get( 1 ) );
			}
			dollars.add( dollar );
		} else {
			Dollar existing = dollars.get( 0 );
			dollars.add( Treasury.getTreasury( existing ).merge( dollar, existing ) );
			dollars.remove( existing );
		}

		dollarRepository.save( dollars );

	}

	private Dollar getDollarForAmount( Treasury treasury, Dollar dollar, int amount ) {

		if( dollar.getWorth() < amount ) {
			throw new IllegalArgumentException( String.format( "Unable to split %s from dollar of worth %s.", amount, dollar.getWorth() ) );
		}

		if( dollar.getWorth() == amount ) {
			return dollar;
		}

		List<Dollar> dollars = treasury.split( dollar, amount );

		this.dollars.addAll( dollars );
		this.dollars.remove( dollar );
		dollarRepository.save( this.dollars );

		Dollar result = null;

		for( Dollar candidate : dollars ) {
			if( candidate.getWorth() == amount ) {
				result = candidate;
				break;
			}
		}

		return result;

	}

	public void validate() {

		List<Exception> exceptions = new ArrayList<Exception>();

		List<Dollar> worthless = new ArrayList<Dollar>();

		for( Dollar dollar : dollars ) {

			Treasury treasury = Treasury.getTreasury( dollar );

			try {

				int worth = treasury.evaluate( dollar );
				if( worth <= 0 ) {
					worthless.add( dollar );
					exceptions.add( new WorthlessMoney( dollar ) );
				}

			} catch( NetworkError exception ) {
				exceptions.add( exception );
			}

		}

		for( Dollar dollar : worthless ) {
			dollars.remove( dollar );
		}

		dollarRepository.save( dollars );

		if( !exceptions.isEmpty() ) {
			throw new ManyExceptions( exceptions );
		}

	}

	private void consolidate() {

		if( dollars.size() < 2 ) {
			return;
		}

		Dollar a = dollars.get( 0 );
		Dollar b = dollars.get( 1 );

		Treasury treasury = Treasury.getTreasury( a );
		Dollar merged = treasury.merge( a, b );

		dollars.remove( a );
		dollars.remove( b );
		dollars.add( merged );

		dollarRepository.save( dollars );

	}

}
