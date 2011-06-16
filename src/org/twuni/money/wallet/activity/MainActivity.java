package org.twuni.money.wallet.activity;

import java.util.Arrays;
import java.util.Set;

import org.twuni.money.common.SimpleToken;
import org.twuni.money.common.Token;
import org.twuni.money.common.Treasury;
import org.twuni.money.common.exception.ManyExceptions;
import org.twuni.money.wallet.R;
import org.twuni.money.wallet.application.WalletApplication;
import org.twuni.money.wallet.application.WalletApplication.Action;
import org.twuni.money.wallet.application.WalletApplication.Extra;
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

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity {

	private static final int REQUEST_CODE_DEPOSIT = 6340517;
	private static final int REQUEST_CODE_WITHDRAW = 41748284;

	private WalletApplication application;
	private Treasury treasury;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );

		application = (WalletApplication) getApplication();

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

			case REQUEST_CODE_WITHDRAW:

				if( resultCode == RESULT_OK ) {
					IntentIntegrator.shareText( this, data.getStringExtra( Extra.TOKEN.toString() ) );
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
							for( Treasury treasury : application.getTreasuries() ) {
								application.getBank( treasury ).validate();
							}
						} catch( ManyExceptions exceptions ) {
							for( Exception exception : exceptions.getExceptions() ) {
								handleException( exception );
							}
						}

						runOnUiThread( new Runnable() {

							@Override
							public void run() {
								setTreasuries( application.getTreasuries() );
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
			showBalance( 0 );
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
				showBalance( application.getBank( treasury ).getBalance() );
			}

			@Override
			public void onNothingSelected( AdapterView<?> parent ) {
				showBalance( 0 );
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
		int amount = getValueFromEditText( R.id.paymentAmount );
		if( amount <= 0 || treasury == null ) {
			return;
		}
		withdraw( amount, treasury.toString() );
	}

	private void handleBarcodeScan( IntentResult result ) {

		if( result == null || result.getContents() == null ) {
			return;
		}

		try {
			deposit( new Gson().fromJson( result.getContents(), SimpleToken.class ) );
		} catch( JsonParseException exception ) {
			handleException( new IllegalArgumentException( "The barcode you scanned is invalid.", exception ) );
		}

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

	private void deposit( Token token ) {

		Intent intent = new Intent( Action.DEPOSIT.toString() );

		intent.putExtra( Extra.TOKEN.toString(), new Gson().toJson( token ) );

		startActivityForResult( intent, REQUEST_CODE_DEPOSIT );

	}

	private void withdraw( int amount, String treasury ) {

		Intent intent = new Intent( Action.WITHDRAW.toString() );

		intent.putExtra( Extra.AMOUNT.toString(), amount );
		intent.putExtra( Extra.TREASURY.toString(), treasury );

		startActivityForResult( intent, REQUEST_CODE_WITHDRAW );

	}

}
