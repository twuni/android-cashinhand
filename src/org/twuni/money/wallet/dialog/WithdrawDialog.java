package org.twuni.money.wallet.dialog;

import org.twuni.money.wallet.R;
import org.twuni.money.wallet.application.WalletApplication;
import org.twuni.money.wallet.util.DebugUtils;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class WithdrawDialog extends Dialog {

	public static final int ID = 41740284;

	public WithdrawDialog( Context context, final WalletApplication application, final String treasury ) {

		super( context );

		requestWindowFeature( Window.FEATURE_NO_TITLE );

		setContentView( R.layout.withdraw_dialog );

		EditText input = (EditText) findViewById( R.id.withdraw_amount );

		input.setOnEditorActionListener( new OnEditorActionListener() {

			@Override
			public boolean onEditorAction( TextView view, int actionId, KeyEvent event ) {

				switch( actionId ) {

					case EditorInfo.IME_ACTION_SEND:

						try {

							int amount = Integer.parseInt( view.getText().toString() );

							if( amount <= 0 ) {
								return false;
							}

							application.withdraw( getOwnerActivity(), amount, treasury );
							dismiss();
							return true;

						} catch( NumberFormatException exception ) {
						} catch( Exception exception ) {
							DebugUtils.handleException( getOwnerActivity(), exception );
						}

				}

				return false;

			}

		} );

		input.setOnFocusChangeListener( new OnFocusChangeListener() {

			@Override
			public void onFocusChange( View view, boolean hasFocus ) {
				if( hasFocus ) {
					getWindow().setSoftInputMode( LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE );
				}
			}

		} );

	}

	@Override
	protected void onStart() {
		super.onStart();
		EditText input = (EditText) findViewById( R.id.withdraw_amount );
		input.setText( "" );
	}

}
