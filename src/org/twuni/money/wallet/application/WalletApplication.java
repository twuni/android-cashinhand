package org.twuni.money.wallet.application;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.client.HttpClient;
import org.twuni.money.common.Bank;
import org.twuni.money.common.SimpleToken;
import org.twuni.money.common.Token;
import org.twuni.money.common.Treasury;
import org.twuni.money.common.TreasuryClient;
import org.twuni.money.wallet.PreferencesRepository;
import org.twuni.money.wallet.activity.MainActivity;

import android.app.Application;
import android.content.SharedPreferences;
import android.net.http.AndroidHttpClient;

import com.google.gson.reflect.TypeToken;

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

	private SharedPreferences preferences;
	private HttpClient client;
	private PreferencesRepository<String> treasuries;

	@Override
	public void onCreate() {

		super.onCreate();

		client = AndroidHttpClient.newInstance( "Android/Cash in Hand", this );
		preferences = getSharedPreferences( MainActivity.class.getName(), MODE_PRIVATE );

		treasuries = new PreferencesRepository<String>( preferences, "treasuries", new TypeToken<List<String>>() {
		}.getType() );

	}

	public Bank getBank( Token token ) {
		return getBank( getTreasury( token.getTreasury() ) );
	}

	public Bank getBank( Treasury treasury ) {
		return new Bank( new PreferencesRepository<Token>( preferences, treasury.toString(), new TypeToken<List<SimpleToken>>() {
		}.getType() ), treasury );
	}

	public Treasury getTreasury( String domain ) {
		TreasuryClient treasury = new TreasuryClient( client, domain );
		treasuries.save( domain );
		return treasury;
	}

	public Set<Treasury> getTreasuries() {
		Set<Treasury> result = new HashSet<Treasury>();
		for( String domain : treasuries.list() ) {
			result.add( getTreasury( domain ) );
		}
		return result;
	}

}
