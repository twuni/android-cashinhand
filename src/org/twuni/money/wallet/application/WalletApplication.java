package org.twuni.money.wallet.application;

import org.apache.http.client.HttpClient;
import org.twuni.money.bank.model.Bank;
import org.twuni.money.bank.model.Treasury;
import org.twuni.money.wallet.SharedPreferencesVault;
import org.twuni.money.wallet.TreasuryLocator;
import org.twuni.money.wallet.activity.MainActivity;

import android.app.Application;
import android.content.SharedPreferences;
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
	private Bank bank;

	@Override
	public void onCreate() {

		super.onCreate();

		SharedPreferences preferences = getSharedPreferences( MainActivity.class.getName(), MODE_PRIVATE );

		client = AndroidHttpClient.newInstance( "Android/Cash in Hand", this );
		bank = new Bank( new SharedPreferencesVault( preferences ), new TreasuryLocator( client ) );

	}

	public Bank getBank() {
		return bank;
	}

	public Treasury getTreasury( String domain ) {
		return new Treasury( client, domain );
	}

}
