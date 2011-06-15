package org.twuni.money.wallet.application;

import org.apache.http.client.HttpClient;
import org.twuni.money.bank.model.Bank;
import org.twuni.money.wallet.SharedPreferencesVault;
import org.twuni.money.wallet.TreasuryLocator;
import org.twuni.money.wallet.activity.MainActivity;

import android.app.Application;
import android.content.SharedPreferences;
import android.net.http.AndroidHttpClient;

public class WalletApplication extends Application {

	public static final String ACTION_DEPOSIT = "org.twuni.money.action.DEPOSIT";

	private Bank bank;

	@Override
	public void onCreate() {

		super.onCreate();

		SharedPreferences preferences = getSharedPreferences( MainActivity.class.getName(), MODE_PRIVATE );
		HttpClient client = AndroidHttpClient.newInstance( "Android/Cash in Hand", this );

		bank = new Bank( new SharedPreferencesVault( preferences ), new TreasuryLocator( client ) );

	}

	public Bank getBank() {
		return bank;
	}

}
