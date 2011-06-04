package org.twuni.money.wallet.activity.nfc;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.widget.Toast;

public class NFCPushActivity extends Activity {

	private NfcAdapter adapter;
	private NdefMessage message;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		try {
			createMessageFromIntent();
		} catch( RuntimeException exception ) {
			handleException( exception );
		}
	}

	private void createMessageFromIntent() {

		adapter = NfcAdapter.getDefaultAdapter( this );

		if( adapter == null ) {
			throw new RuntimeException( "NFC is not supported on this device." );
		}

		String shared = getSharedTextFromIntent();

		try {
			message = createMessage( shared );
		} catch( UnsupportedEncodingException exception ) {
			throw new RuntimeException( exception );
		}

	}

	private String getSharedTextFromIntent() {

		String shared = null;

		Intent intent = getIntent();

		if( intent != null && intent.getExtras() != null ) {
			shared = getIntent().getExtras().getString( "share" );
		}

		if( shared == null ) {
			throw new RuntimeException( "No text found to share." );
		}

		return shared;

	}

	private void handleException( Exception exception ) {
		Toast.makeText( this, String.format( "%s", exception.getMessage() ), Toast.LENGTH_SHORT ).show();
		onBackPressed();
	}

	@Override
	protected void onPause() {
		super.onPause();
		adapter.disableForegroundNdefPush( this );
	}

	@Override
	protected void onResume() {
		super.onResume();
		adapter.enableForegroundNdefPush( this, message );
	}

	private NdefMessage createMessage( String text ) throws UnsupportedEncodingException {
		return createMessage( text, Locale.US );
	}

	private NdefMessage createMessage( String text, Locale locale ) throws UnsupportedEncodingException {
		NdefRecord record = new NdefRecord( NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte [] {}, toNdefRecordData( text, locale ) );
		NdefMessage message = new NdefMessage( new NdefRecord [] { record } );
		return message;
	}

	private byte [] toNdefRecordData( String text, Locale locale ) throws UnsupportedEncodingException {

		byte [] language = locale.getLanguage().getBytes( "US-ASCII" );
		byte [] rawText = text.getBytes( "UTF-8" );

		byte [] data = new byte [1 + language.length + rawText.length];

		data[0] = (byte) language.length;
		System.arraycopy( language, 0, data, 1, language.length );
		System.arraycopy( rawText, 0, data, 1 + language.length, rawText.length );

		return data;

	}

}
