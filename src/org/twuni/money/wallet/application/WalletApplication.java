package org.twuni.money.wallet.application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.client.HttpClient;
import org.twuni.money.common.Bank;
import org.twuni.money.common.Token;
import org.twuni.money.common.Treasury;
import org.twuni.money.common.exception.ManyExceptions;
import org.twuni.money.treasury.client.TreasuryClient;
import org.twuni.money.wallet.R;
import org.twuni.money.wallet.activity.DepositActivity;
import org.twuni.money.wallet.activity.WithdrawActivity;
import org.twuni.money.wallet.adapter.TreasuryView;
import org.twuni.money.wallet.repository.TokenRepository;
import org.twuni.money.wallet.repository.TreasuryRepository;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.http.AndroidHttpClient;

public class WalletApplication extends Application {

	public static enum Action {

		DEPOSIT( "org.twuni.money.action.DEPOSIT" ),
		WITHDRAW( "org.twuni.money.action.WITHDRAW" ),
		SCAN( "com.google.zxing.client.android.SCAN" ),
		RECEIVE( "android.intent.action.RECEIVE" ),
		SHARE( Intent.ACTION_SEND );

		private final String action;

		private Action( String action ) {
			this.action = action;
		}

		@Override
		public String toString() {
			return action;
		};

	}

	public static enum Extra {

		TOKEN,
		AMOUNT,
		TREASURY,
		SCAN_MODE,
		SCAN_RESULT;

	}

	public static enum Request {

		WITHDRAW,
		DEPOSIT,
		SCAN,
		SHARE,
		RECEIVE;

		public static Request valueOf( int hashCode ) {
			for( Request request : values() ) {
				if( request.hashCode() == hashCode ) {
					return request;
				}
			}
			return null;
		}

	}

	private HttpClient client;
	private TreasuryRepository treasuryRepository;

	@Override
	public void onCreate() {

		super.onCreate();

		client = AndroidHttpClient.newInstance( "Android/Cash in Hand", this );
		treasuryRepository = new TreasuryRepository( this );

	}

	public Bank getBank( Token token ) {
		return getBank( getTreasury( token.getTreasury() ), token.getTreasury() );
	}

	public Bank getBank( Treasury treasury, String treasuryUrl ) {
		return new Bank( new TokenRepository( this, treasuryUrl ), treasury );
	}

	public Treasury getTreasury( String domain ) {
		return new TreasuryClient( client, domain );
	}

	public Set<Treasury> getTreasuries() {
		Set<Treasury> treasuries = new HashSet<Treasury>();
		for( String url : treasuryRepository.list() ) {
			treasuries.add( getTreasury( url ) );
		}
		return treasuries;
	}

	public Set<Bank> getBanks() {
		Set<Bank> banks = new HashSet<Bank>();
		for( String url : treasuryRepository.list() ) {
			banks.add( new Bank( new TokenRepository( this, url ), getTreasury( url ) ) );
		}
		return banks;
	}

	public void checkIntegrity() {
		List<Exception> exceptions = new ArrayList<Exception>();
		for( Bank bank : getBanks() ) {
			try {
				bank.validate();
			} catch( ManyExceptions exception ) {
				exceptions.add( exception );
			}
		}
		if( !exceptions.isEmpty() ) {
			throw new ManyExceptions( exceptions );
		}
	}

	public List<TreasuryView> getBalance() {
		List<TreasuryView> views = new ArrayList<TreasuryView>();
		for( String url : treasuryRepository.list() ) {
			int balance = getBalance( url );
			if( balance <= 0 ) {
				continue;
			}
			views.add( new TreasuryView( url, balance ) );
		}
		return views;
	}

	public int getBalance( String treasury ) {
		TokenRepository tokenRepository = treasuryRepository.getTokenRepository( treasury );
		List<Token> tokens = tokenRepository.list();
		int balance = 0;
		for( Token token : tokens ) {
			balance += token.getValue();
		}
		return balance;
	}

	public void deleteTreasury( String treasury ) {
		treasuryRepository.delete( treasury );
	}

	public void receive( Activity activity ) {
		Intent intent = new Intent( Action.RECEIVE.toString() );
		intent.setType( "text/plain" );
		startActivityForResult( activity, intent, R.string.receive_money, Request.RECEIVE );
	}

	public void deposit( Activity activity, String tokenString ) {
		Intent intent = new Intent( activity, DepositActivity.class );
		intent.putExtra( Intent.EXTRA_TEXT, tokenString );
		startActivityForResult( activity, intent, Request.DEPOSIT );
	}

	public void withdraw( Activity activity, int amount, String treasury ) {
		Intent intent = new Intent( activity, WithdrawActivity.class );
		intent.putExtra( Extra.AMOUNT.toString(), amount );
		intent.putExtra( Extra.TREASURY.toString(), treasury );
		startActivityForResult( activity, intent, Request.WITHDRAW );
	}

	public void share( Activity activity, String text ) {
		Intent intent = new Intent( Action.SHARE.toString() );
		intent.setType( "text/plain" );
		intent.putExtra( Intent.EXTRA_TEXT, text );
		startActivityForResult( activity, intent, R.string.share_via, Request.SHARE );
	}

	private void startActivityForResult( Activity activity, Intent intent, Request request ) {
		activity.startActivityForResult( intent, request.hashCode() );
	}

	private void startActivityForResult( Activity activity, Intent intent, int chooserLabelId, Request request ) {
		activity.startActivityForResult( createChooser( activity, intent, chooserLabelId ), request.hashCode() );
	}

	private Intent createChooser( Context context, Intent intent, int labelId ) {
		return Intent.createChooser( intent, context.getString( labelId ) );
	}

}
