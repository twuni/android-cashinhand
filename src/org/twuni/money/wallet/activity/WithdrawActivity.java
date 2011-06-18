package org.twuni.money.wallet.activity;

import org.twuni.money.common.Token;
import org.twuni.money.common.Treasury;
import org.twuni.money.wallet.R;
import org.twuni.money.wallet.application.WalletApplication;
import org.twuni.money.wallet.application.WalletApplication.Extra;
import org.twuni.money.wallet.task.BankTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.google.gson.Gson;

public class WithdrawActivity extends Activity {

	@Override
	protected void onCreate( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );

		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( R.layout.loading );

		new BankTask<Token>( this ) {

			@Override
			protected Token handleIntent( Intent intent ) {

				WalletApplication application = (WalletApplication) getApplication();
				int amount = intent.getIntExtra( Extra.AMOUNT.toString(), 0 );
				Treasury treasury = application.getTreasury( intent.getStringExtra( Extra.TREASURY.toString() ) );

				return application.getBank( treasury ).withdraw( amount );

			}

			@Override
			protected void putExtras( Intent data, Token token ) {

				String tokenString = new Gson().toJson( token );

				data.putExtra( Extra.TOKEN.toString(), tokenString );

				WalletApplication application = (WalletApplication) getApplication();
				application.share( WithdrawActivity.this, tokenString );

			}

		}.execute( getIntent() );

	}

}
