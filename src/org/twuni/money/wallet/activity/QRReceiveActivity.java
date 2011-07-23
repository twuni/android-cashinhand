package org.twuni.money.wallet.activity;

import org.twuni.money.wallet.application.WalletApplication.Action;
import org.twuni.money.wallet.application.WalletApplication.Extra;
import org.twuni.money.wallet.application.WalletApplication.Request;
import org.twuni.money.wallet.dialog.InstallDialog;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;

public class QRReceiveActivity extends Activity {

	@Override
	protected void onCreate( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );

		if( savedInstanceState != null ) {
			return;
		}

		Intent intent = new Intent( Action.SCAN.toString() );

		intent.putExtra( Extra.SCAN_MODE.toString(), "QR_CODE_MODE" );

		try {
			startActivityForResult( intent, Request.SCAN.hashCode() );
		} catch( ActivityNotFoundException exception ) {
			new InstallDialog( this, "com.google.zxing.client.android", "Barcode Scanner" ).show();
		}

	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) {

		if( resultCode != RESULT_OK ) {
			super.onActivityResult( requestCode, resultCode, data );
			setResult( RESULT_CANCELED, new Intent() );
			finish();
			return;
		}

		switch( Request.valueOf( requestCode ) ) {
			case SCAN:
				setTextResult( data.getStringExtra( Extra.SCAN_RESULT.toString() ) );
				break;
		}

	}

	private void setTextResult( String text ) {
		Intent result = new Intent();
		result.putExtra( Intent.EXTRA_TEXT, text );
		setResult( RESULT_OK, result );
		finish();
	}

}
