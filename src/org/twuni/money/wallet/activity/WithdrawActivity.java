package org.twuni.money.wallet.activity;

import org.twuni.money.common.Token;
import org.twuni.money.common.Treasury;
import org.twuni.money.wallet.R;
import org.twuni.money.wallet.application.WalletApplication;
import org.twuni.money.wallet.application.WalletApplication.Extra;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.google.gson.Gson;

public class WithdrawActivity extends Activity {

	@Override
	protected void onCreate( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );

		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.loading );

		Intent intent = getIntent();

		final WalletApplication application = (WalletApplication) getApplication();
		final int amount = intent.getIntExtra( Extra.AMOUNT.toString(), 0 );
		final Treasury treasury = application.getTreasury( intent.getStringExtra( Extra.TREASURY.toString() ) );

		new Thread() {

			@Override
			public void run() {

				try {

					Token token = application.getBank( treasury ).withdraw( amount );
					String tokenString = new Gson().toJson( token );

					application.share( WithdrawActivity.this, tokenString );

					Intent data = new Intent();
					data.putExtra( Extra.TOKEN.toString(), tokenString );
					setResult( RESULT_OK, data );

				} catch( Exception exception ) {
					handleException( exception.getMessage() );
					setResult( RESULT_CANCELED );
				}

				finish();

			}

		}.start();

	}

	private void handleException( String pattern, Object... args ) {

		final String message = String.format( pattern, args );

		runOnUiThread( new Runnable() {

			@Override
			public void run() {
				Toast.makeText( WithdrawActivity.this, message, Toast.LENGTH_SHORT ).show();
			}

		} );

	}
}
