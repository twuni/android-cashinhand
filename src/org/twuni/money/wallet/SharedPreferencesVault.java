package org.twuni.money.wallet;

import java.util.List;

import org.twuni.money.bank.model.Dollar;
import org.twuni.money.bank.model.Vault;
import org.twuni.money.bank.util.JsonUtils;

import android.content.SharedPreferences;

public class SharedPreferencesVault implements Vault {

	private static final String PREFERENCES_KEY = "vault";

	private final SharedPreferences preferences;

	public SharedPreferencesVault( SharedPreferences preferences ) {
		this.preferences = preferences;
	}

	@Override
	public void save( List<Dollar> dollars ) {
		preferences.edit().putString( PREFERENCES_KEY, JsonUtils.serialize( dollars ) ).commit();
	}

	@Override
	public List<Dollar> load() {
		return JsonUtils.deserializeList( preferences.getString( PREFERENCES_KEY, "[]" ), Dollar.class );
	}

}
