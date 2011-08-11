package org.twuni.money.wallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;

public class CopyActivity extends Activity {

	@Override
	protected void onCreate( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );

		try {

			ClipboardManager clipboard = (ClipboardManager) getSystemService( CLIPBOARD_SERVICE );
			String text = getIntent().getStringExtra( Intent.EXTRA_TEXT );
			clipboard.setText( text );

			setResult( RESULT_OK );
			finish();

		} catch( Exception exception ) {

			setResult( RESULT_CANCELED );
			finish();

		}

	}

}
