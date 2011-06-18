package org.twuni.money.wallet.activity;

import org.twuni.money.common.SimpleToken;
import org.twuni.money.common.Token;
import org.twuni.money.wallet.R;
import org.twuni.money.wallet.application.WalletApplication;
import org.twuni.money.wallet.application.WalletApplication.Extra;
import org.twuni.money.wallet.task.BankTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.google.gson.Gson;

public class DepositActivity extends Activity {

	@Override
	protected void onCreate( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );

		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( R.layout.loading );

		new BankTask<Integer>( this ) {

			@Override
			protected Integer handleIntent( Intent intent ) {

				WalletApplication application = (WalletApplication) getApplication();
				Token token = new Gson().fromJson( intent.getStringExtra( Extra.TOKEN.toString() ), SimpleToken.class );
				application.getBank( token ).deposit( token );

				return Integer.valueOf( token.getValue() );

			}

			@Override
			protected void putExtras( Intent data, Integer amount ) {
				data.putExtra( Extra.AMOUNT.toString(), amount.intValue() );
			}

		}.execute( getIntent() );

	}

}
