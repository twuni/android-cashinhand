package org.twuni.money.wallet.activity;

import java.util.Arrays;
import java.util.Set;

import org.twuni.money.bank.exception.InsufficientFunds;
import org.twuni.money.bank.exception.ManyExceptions;
import org.twuni.money.bank.exception.NetworkError;
import org.twuni.money.bank.model.Bank;
import org.twuni.money.bank.model.Dollar;
import org.twuni.money.bank.model.Treasury;
import org.twuni.money.bank.util.JsonUtils;
import org.twuni.money.wallet.R;
import org.twuni.money.wallet.application.WalletApplication;
import org.twuni.money.wallet.util.DebugUtils;

import android.app.Activity;
import android.content.Intent;
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

	private static final int REQUEST_CODE_DEPOSIT = 6340517;

	private Bank bank;
	private Treasury treasury;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );

		bank = ( (WalletApplication) getApplication() ).getBank();

	}

	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) {

		super.onActivityResult( requestCode, resultCode, data );

		switch( requestCode ) {

			case REQUEST_CODE_DEPOSIT:

				if( resultCode == RESULT_OK ) {
					refresh();
				}

				break;

			default:

				IntentResult result = IntentIntegrator.parseActivityResult( requestCode, resultCode, data );
				if( result != null ) {
					handleBarcodeScan( result );
				}

		}

	}

	protected void refresh() {

		runOnUiThread( new Runnable() {

			@Override
			public void run() {

				beginLoading();

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
							}

						} );

					}

				}.start();

			}

		} );

	}

	private void beginLoading() {
		findViewById( R.id.loading ).setVisibility( View.VISIBLE );
		findViewById( R.id.treasury ).setVisibility( View.GONE );
		findViewById( R.id.balance ).setVisibility( View.GONE );
		findViewById( R.id.empty ).setVisibility( View.GONE );
	}

	private void finishLoading() {
		findViewById( R.id.loading ).setVisibility( View.GONE );
	}

	private void showBalance( int balance ) {
		String text = toCurrencyString( balance );
		TextView view = (TextView) findViewById( R.id.balance );
		view.setText( text );
		view.setVisibility( View.VISIBLE );
		if( balance <= 0 ) {
			findViewById( R.id.empty ).setVisibility( View.VISIBLE );
		}
		finishLoading();
	}

	private void setTreasuries( Set<Treasury> treasuries ) {

		if( treasuries.isEmpty() ) {
			showBalance( bank.getBalance() );
			return;
		}

		Spinner treasurySelector = (Spinner) findViewById( R.id.treasury );

		ArrayAdapter<Treasury> adapter = new ArrayAdapter<Treasury>( this, android.R.layout.simple_spinner_item, Arrays.asList( treasuries.toArray( new Treasury [0] ) ) );

		adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		treasurySelector.setAdapter( adapter );

		treasurySelector.setOnItemSelectedListener( new OnItemSelectedListener() {

			@Override
			public void onItemSelected( AdapterView<?> parent, View view, int index, long id ) {
				final Treasury treasury = (Treasury) parent.getItemAtPosition( index );
				MainActivity.this.treasury = treasury;
				showBalance( bank.getBalance( treasury ) );
			}

			@Override
			public void onNothingSelected( AdapterView<?> parent ) {
				showBalance( bank.getBalance() );
			}

		} );

		treasurySelector.setVisibility( View.VISIBLE );

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
						deposit( JsonUtils.deserialize( contents, Dollar.class ) );
					} catch( ClassCastException exception ) {
						handleException( new IllegalArgumentException( "The code you scanned is not a valid dollar.", exception ) );
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

	private void deposit( Dollar dollar ) {
		Intent intent = new Intent( WalletApplication.ACTION_DEPOSIT );
		intent.putExtra( "token", JsonUtils.serialize( dollar ) );
		startActivityForResult( intent, REQUEST_CODE_DEPOSIT );
	}

}
