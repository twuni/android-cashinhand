package org.twuni.money.wallet.activity;

import org.apache.http.client.HttpClient;
import org.twuni.money.bank.exception.InsufficientFunds;
import org.twuni.money.bank.exception.ManyExceptions;
import org.twuni.money.bank.exception.NetworkError;
import org.twuni.money.bank.model.Bank;
import org.twuni.money.bank.model.Dollar;
import org.twuni.money.bank.util.JsonUtils;
import org.twuni.money.wallet.R;
import org.twuni.money.wallet.SharedPreferencesVault;
import org.twuni.money.wallet.TreasuryLocator;
import org.twuni.money.wallet.util.DebugUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity {

	private Bank bank;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );

		SharedPreferences preferences = getSharedPreferences( getClass().getName(), MODE_PRIVATE );
		HttpClient client = AndroidHttpClient.newInstance( "Android/Twuni", this );

		bank = new Bank( new SharedPreferencesVault( preferences ), new TreasuryLocator( client ) );

	}

	@Override
	protected void onResume() {

		super.onResume();
		final Context context = this;

		new Thread() {

			@Override
			public void run() {
				try {
					bank.validate();
				} catch( ManyExceptions exceptions ) {
					for( Exception exception : exceptions.getExceptions() ) {
						handleException( context, exception );
					}
				}

				runOnUiThread( new Runnable() {

					@Override
					public void run() {
						TextView balance = (TextView) findViewById( R.id.balance );
						balance.setText( toCurrencyString( bank.getBalance() ) );
					}

				} );

			}

		}.start();

	}

	private String toCurrencyString( int balance ) {
		return String.format( "$%.2f", Double.valueOf( balance / 100.0 ) );
	}

	public void launchDeposit( View view ) {
		IntentIntegrator.initiateScan( this );
	}

	public void launchPayment( View view ) {

		final Activity context = this;

		new Thread() {

			@Override
			public void run() {
				try {
					int value = getValueFromEditText( R.id.paymentAmount );
					if( value <= 0 ) {
						return;
					}
					IntentIntegrator.shareText( context, JsonUtils.serialize( bank.withdraw( value ) ) );
				} catch( InsufficientFunds exception ) {
					handleException( context, exception );
				} catch( NetworkError exception ) {
					handleException( context, exception );
				}
			}

		}.start();

	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
		super.onActivityResult( requestCode, resultCode, data );
		IntentResult result = IntentIntegrator.parseActivityResult( requestCode, resultCode, data );
		handleBarcodeScan( result );
	}

	private void handleBarcodeScan( IntentResult result ) {

		final String contents = result.getContents();
		final Context context = this;

		new Thread() {

			@Override
			public void run() {
				if( contents != null ) {
					try {
						bank.deposit( JsonUtils.deserialize( contents, Dollar.class ) );
					} catch( NetworkError exception ) {
						handleException( context, exception );
					} catch( ClassCastException exception ) {
						handleException( context, new IllegalArgumentException( "The barcode you scanned is not a valid dollar.", exception ) );
					} catch( Exception exception ) {
						handleException( context, exception );
					}
				}
			}

		}.start();

	}

	private int getValueFromEditText( int resourceId ) {
		EditText field = (EditText) findViewById( resourceId );
		Editable value = field.getText();
		try {
			return (int) ( Double.parseDouble( value.toString() ) * 100 );
		} catch( NumberFormatException exception ) {
			return 0;
		}
	}
	
	private void handleException( final Context context, final Exception exception ) {
		runOnUiThread( new Runnable() {
			@Override
			public void run() {
				DebugUtils.handleException( context, exception );
			}
		} );
	}

}
