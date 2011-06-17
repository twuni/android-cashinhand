package org.twuni.money.wallet.activity;

import org.twuni.money.common.SimpleToken;
import org.twuni.money.common.Token;
import org.twuni.money.wallet.R;
import org.twuni.money.wallet.application.WalletApplication;
import org.twuni.money.wallet.application.WalletApplication.Extra;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.google.gson.Gson;

public class DepositActivity extends Activity {

	@Override
	protected void onCreate( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );

		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.loading );

		Intent intent = getIntent();

		final WalletApplication application = (WalletApplication) getApplication();
		final Token token = new Gson().fromJson( intent.getStringExtra( Extra.TOKEN.toString() ), SimpleToken.class );

		new Thread() {

			@Override
			public void run() {

				try {
					application.getBank( token ).deposit( token );
					toast( "Deposited %s into the vault for %s.", toCurrencyString( token.getValue() ), token.getTreasury() );
					Intent data = new Intent();
					data.putExtra( Extra.AMOUNT.toString(), token.getValue() );
					setResult( RESULT_OK, data );
				} catch( Exception exception ) {
					toast( exception.getMessage() );
					setResult( RESULT_CANCELED );
				}

				finish();

			}

		}.start();

	}

	private void toast( String pattern, Object... args ) {

		final String message = String.format( pattern, args );

		runOnUiThread( new Runnable() {

			@Override
			public void run() {
				Toast.makeText( DepositActivity.this, message, Toast.LENGTH_SHORT ).show();
			}

		} );

	}

	private String toCurrencyString( int balance ) {
		return String.format( "$%.2f", Double.valueOf( balance / 100.0 ) );
	}

}
