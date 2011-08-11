package org.twuni.money.wallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;

public class PasteActivity extends Activity {

	@Override
	protected void onCreate( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );

		ClipboardManager clipboard = (ClipboardManager) getSystemService( CLIPBOARD_SERVICE );

		if( clipboard.hasText() ) {
			Intent data = new Intent();
			data.putExtra( Intent.EXTRA_TEXT, clipboard.getText() );
			setResult( RESULT_OK, data );
			finish();
		} else {
			setResult( RESULT_CANCELED );
			finish();
		}

	}

}
