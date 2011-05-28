package org.twuni.money.wallet.activity;

import java.util.List;

import org.twuni.money.wallet.R;
import org.twuni.money.bank.exception.InsufficientFunds;
import org.twuni.money.bank.exception.ManyExceptions;
import org.twuni.money.bank.exception.NetworkError;
import org.twuni.money.bank.model.Bank;
import org.twuni.money.bank.model.Dollar;
import org.twuni.money.bank.model.Vault;
import org.twuni.money.bank.util.JsonUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity {

	private Bank bank;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );

		final SharedPreferences preferences = getSharedPreferences( getClass().getName(), MODE_PRIVATE );

		bank = new Bank( new Vault() {

			@Override
			public void save( List<Dollar> dollars ) {
				preferences.edit().putString( Bank.class.getName(), JsonUtils.serialize( dollars ) ).commit();
			}

			@Override
			public List<Dollar> load() {
				return JsonUtils.deserializeList( preferences.getString( Bank.class.getName(), "[]" ), Dollar.class );
			}

		} );

	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			bank.validate();
		} catch( ManyExceptions exceptions ) {
			for( Exception exception : exceptions.getExceptions() ) {
				handleException( exception );
			}
		}
		TextView balance = (TextView) findViewById( R.id.balance );
		balance.setText( toCurrencyString( bank.getBalance() ) );
	}

	private String toCurrencyString( int balance ) {
		return String.format( "$%.2f", Double.valueOf( balance / 100.0 ) );
	}

	public void launchScan( View view ) {
		IntentIntegrator.initiateScan( this );
	}

	public void launchPayment( View view ) {
		try {
			int value = getValueFromEditText( R.id.paymentAmount );
			if( value <= 0 ) {
				return;
			}
			IntentIntegrator.shareText( this, JsonUtils.serialize( bank.withdraw( value ) ) );
		} catch( InsufficientFunds exception ) {
			handleException( exception );
		} catch( NetworkError exception ) {
			handleException( exception );
		}
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
		super.onActivityResult( requestCode, resultCode, data );
		IntentResult result = IntentIntegrator.parseActivityResult( requestCode, resultCode, data );
		handleBarcodeScan( result );
	}

	private void handleBarcodeScan( IntentResult result ) {
		String contents = result.getContents();
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

	private int getValueFromEditText( int resourceId ) {
		EditText field = (EditText) findViewById( resourceId );
		Editable value = field.getText();
		try {
			return (int) ( Double.parseDouble( value.toString() ) * 100 );
		} catch( NumberFormatException exception ) {
			return 0;
		}
	}

	private void handleException( Exception exception ) {
		Toast.makeText( this, exception.getMessage(), Toast.LENGTH_SHORT ).show();
	}

}
