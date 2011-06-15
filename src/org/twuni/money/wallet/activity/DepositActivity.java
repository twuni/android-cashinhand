package org.twuni.money.wallet.activity;

import org.twuni.money.bank.model.Dollar;
import org.twuni.money.bank.util.JsonUtils;
import org.twuni.money.wallet.R;
import org.twuni.money.wallet.application.WalletApplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

public class DepositActivity extends Activity {

	@Override
	protected void onCreate( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );

		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.loading );

		Intent intent = getIntent();

		final Dollar dollar = JsonUtils.deserialize( intent.getStringExtra( "token" ), Dollar.class );

		new Thread() {

			@Override
			public void run() {

				try {
					( (WalletApplication) getApplication() ).getBank().deposit( dollar );
					toast( "The %s token %s issued by %s has been deposited.", toCurrencyString( dollar.getWorth() ), dollar.getId(), dollar.getTreasury() );
					Intent data = new Intent();
					data.putExtra( "amount", dollar.getWorth() );
					setResult( RESULT_OK, data );
				} catch( Exception exception ) {
					toast( exception.getMessage() );
					setResult( RESULT_CANCELED );
				}

				onBackPressed();

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
