package org.twuni.money.wallet.dialog;

import org.twuni.money.wallet.R;
import org.twuni.money.wallet.application.WalletApplication;
import org.twuni.money.wallet.util.DebugUtils;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class WithdrawDialog extends Dialog {

	public static final int ID = 41740284;

	public WithdrawDialog( Context context, final WalletApplication application, final String treasury ) {

		super( context );

		setContentView( R.layout.withdraw_dialog );
		setTitle( String.format( context.getString( R.string.withdraw_from ), treasury ) );

		EditText input = (EditText) findViewById( R.id.withdraw_amount );

		input.setOnEditorActionListener( new OnEditorActionListener() {

			@Override
			public boolean onEditorAction( TextView view, int actionId, KeyEvent event ) {

				switch( actionId ) {

					case EditorInfo.IME_ACTION_SEND:

						try {

							int amount = (int) ( Double.parseDouble( view.getText().toString() ) * 100 );

							if( amount <= 0 ) {
								return false;
							}

							application.withdraw( getOwnerActivity(), amount, treasury );
							dismiss();
							return true;

						} catch( Exception exception ) {
							DebugUtils.handleException( getOwnerActivity(), exception );
						}

				}

				return false;

			}

		} );

	}

}
