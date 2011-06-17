package org.twuni.money.wallet.application;

import java.util.HashSet;
import java.util.Set;

import org.apache.http.client.HttpClient;
import org.twuni.money.common.Bank;
import org.twuni.money.common.Token;
import org.twuni.money.common.Treasury;
import org.twuni.money.common.TreasuryClient;
import org.twuni.money.wallet.repository.TokenRepository;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.http.AndroidHttpClient;

public class WalletApplication extends Application {

	public static enum Action {

		DEPOSIT,
		WITHDRAW;

		@Override
		public String toString() {
			return String.format( "org.twuni.money.action.%s", name() );
		};

	}

	public static enum Extra {

		TOKEN,
		AMOUNT,
		TREASURY;

	}

	private HttpClient client;
	private TokenRepository repository;

	@Override
	public void onCreate() {

		super.onCreate();

		client = AndroidHttpClient.newInstance( "Android/Cash in Hand", this );
		repository = new TokenRepository( this );

	}

	public Bank getBank( Token token ) {
		return getBank( getTreasury( token.getTreasury() ) );
	}

	public Bank getBank( Treasury treasury ) {
		return new Bank( repository, treasury );
	}

	public Treasury getTreasury( String domain ) {
		return new TreasuryClient( client, domain );
	}

	public Set<Treasury> getTreasuries() {
		SQLiteDatabase database = repository.getReadableDatabase();
		Cursor cursor = database.rawQuery( "SELECT DISTINCT treasury FROM token", new String [0] );
		Set<Treasury> treasuries = new HashSet<Treasury>();
		while( cursor.moveToNext() ) {
			treasuries.add( new TreasuryClient( client, cursor.getString( 0 ) ) );
		}
		return treasuries;
	}

	public Set<Bank> getBanks() {
		Set<Bank> banks = new HashSet<Bank>();
		for( Treasury treasury : getTreasuries() ) {
			banks.add( getBank( treasury ) );
		}
		return banks;
	}

	public Cursor executeQuery( String sql, String... params ) {
		return repository.getReadableDatabase().rawQuery( sql, params );
	}

}
