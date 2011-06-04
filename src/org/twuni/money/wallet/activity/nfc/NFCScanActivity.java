package org.twuni.money.wallet.activity.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Bundle;

public class NFCScanActivity extends Activity {

	private NfcAdapter adapter;
	private PendingIntent pendingIntent;
	private IntentFilter [] filters;
	private String [][] techLists;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );

		adapter = NfcAdapter.getDefaultAdapter( this );

		pendingIntent = PendingIntent.getActivity( this, 0, new Intent( this, getClass() ).addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP ), 0 );

		try {
			IntentFilter filter = new IntentFilter( NfcAdapter.ACTION_NDEF_DISCOVERED );
			filter.addDataType( "application/json+money" );
			filters = new IntentFilter [] { filter };
		} catch( MalformedMimeTypeException exception ) {
			throw new RuntimeException( exception );
		}

		techLists = new String [] [] { new String [] { NfcF.class.getName() } };

	}

	@Override
	protected void onPause() {
		super.onPause();
		adapter.disableForegroundDispatch( this );
	}

	@Override
	protected void onResume() {
		super.onResume();
		adapter.enableForegroundDispatch( this, pendingIntent, filters, techLists );
	}

	@Override
	protected void onNewIntent( Intent intent ) {
		// Tag tag = intent.getParcelableExtra( NfcAdapter.EXTRA_TAG );
		// TODO: Do some stuff.
	}

}
