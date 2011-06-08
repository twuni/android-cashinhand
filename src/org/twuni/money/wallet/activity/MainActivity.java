package org.twuni.money.wallet.activity;

import java.util.Arrays;
import java.util.Set;

import org.apache.http.client.HttpClient;
import org.twuni.money.bank.exception.InsufficientFunds;
import org.twuni.money.bank.exception.ManyExceptions;
import org.twuni.money.bank.exception.NetworkError;
import org.twuni.money.bank.model.Bank;
import org.twuni.money.bank.model.Dollar;
import org.twuni.money.bank.model.Treasury;
import org.twuni.money.bank.util.JsonUtils;
import org.twuni.money.wallet.R;
import org.twuni.money.wallet.SharedPreferencesVault;
import org.twuni.money.wallet.TreasuryLocator;
import org.twuni.money.wallet.util.DebugUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity {

	private Bank bank;
	private Treasury treasury;

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

		new Thread() {

			@Override
			public void run() {
				try {
					bank.validate();
				} catch( ManyExceptions exceptions ) {
					for( Exception exception : exceptions.getExceptions() ) {
						handleException( exception );
					}
				}

				runOnUiThread( new Runnable() {

					@Override
					public void run() {

						setTreasuries( bank.getTreasuries() );

						if( treasury != null ) {
							setBalance( bank.getBalance( treasury ) );
						} else {
							setBalance( bank.getBalance() );
						}

					}

				} );

			}

		}.start();

	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
		super.onActivityResult( requestCode, resultCode, data );
		IntentResult result = IntentIntegrator.parseActivityResult( requestCode, resultCode, data );
		handleBarcodeScan( result );
	}

	private void setBalance( int balance ) {
		String text = toCurrencyString( balance );
		TextView view = (TextView) findViewById( R.id.balance );
		view.setText( text );
	}

	private void setTreasuries( Set<Treasury> treasuries ) {
		
		Spinner spinner = (Spinner) findViewById( R.id.treasury );

		if( treasuries.isEmpty() ) {
			spinner.setVisibility( View.GONE );
			findViewById( R.id.empty ).setVisibility( View.VISIBLE );
			setBalance( bank.getBalance() );
			return;
		}

		findViewById( R.id.empty ).setVisibility( View.GONE );
		spinner.setVisibility( View.VISIBLE );

		ArrayAdapter<Treasury> adapter = new ArrayAdapter<Treasury>( this, android.R.layout.simple_spinner_item, Arrays.asList( treasuries.toArray( new Treasury [0] ) ) );

		adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		spinner.setAdapter( adapter );

		spinner.setOnItemSelectedListener( new OnItemSelectedListener() {

			@Override
			public void onItemSelected( AdapterView<?> parent, View view, int index, long id ) {
				Treasury treasury = (Treasury) parent.getItemAtPosition( index );
				MainActivity.this.treasury = treasury;
				setBalance( bank.getBalance( treasury ) );
			}

			@Override
			public void onNothingSelected( AdapterView<?> parent ) {
				setBalance( bank.getBalance() );
			}

		} );

	}

	private String toCurrencyString( int balance ) {
		return String.format( "$%.2f", Double.valueOf( balance / 100.0 ) );
	}

	public void launchDeposit( View view ) {
		IntentIntegrator.initiateScan( this );
	}

	public void launchPayment( View view ) {

		new Thread() {

			@Override
			public void run() {
				try {
					int value = getValueFromEditText( R.id.paymentAmount );
					if( value <= 0 ) {
						return;
					}
					IntentIntegrator.shareText( MainActivity.this, JsonUtils.serialize( bank.withdraw( value, treasury ) ) );
				} catch( InsufficientFunds exception ) {
					handleException( exception );
				} catch( NetworkError exception ) {
					handleException( exception );
				}
			}

		}.start();

	}

	private void handleBarcodeScan( IntentResult result ) {

		final String contents = result.getContents();

		new Thread() {

			@Override
			public void run() {
				if( contents != null ) {
					try {
						bank.deposit( JsonUtils.deserialize( contents, Dollar.class ) );
					} catch( NetworkError exception ) {
						handleException( exception );
					} catch( ClassCastException exception ) {
						handleException( new IllegalArgumentException( "The barcode you scanned is not a valid dollar.", exception ) );
					} catch( Exception exception ) {
						handleException( exception );
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

	private void handleException( final Exception exception ) {

		runOnUiThread( new Runnable() {

			@Override
			public void run() {
				DebugUtils.handleException( MainActivity.this, exception );
			}

		} );

	}

}
