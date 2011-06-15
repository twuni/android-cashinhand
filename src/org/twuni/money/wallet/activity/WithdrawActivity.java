package org.twuni.money.wallet.activity;

import org.twuni.money.bank.model.Dollar;
import org.twuni.money.bank.model.Treasury;
import org.twuni.money.bank.util.JsonUtils;
import org.twuni.money.wallet.R;
import org.twuni.money.wallet.application.WalletApplication;
import org.twuni.money.wallet.application.WalletApplication.Extra;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

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
					Dollar dollar = application.getBank().withdraw( amount, treasury );
					Intent data = new Intent();
					data.putExtra( Extra.TOKEN.toString(), JsonUtils.serialize( dollar ) );
					setResult( RESULT_OK, data );
				} catch( Exception exception ) {
					handleException( exception.getMessage() );
					setResult( RESULT_CANCELED );
				}

				onBackPressed();

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