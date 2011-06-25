package org.twuni.money.wallet.dialog;

import org.twuni.money.wallet.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

public class InstallDialog extends AlertDialog {

	public InstallDialog( final Context activity, final String packageName, String applicationName ) {

		super( activity );

		setTitle( activity.getString( R.string.download_required ) );
		setMessage( String.format( activity.getString( R.string.download_message ), applicationName ) );

		setButton( BUTTON_POSITIVE, activity.getString( android.R.string.yes ), new OnClickListener() {

			public void onClick( DialogInterface dialogInterface, int i ) {
				Uri uri = Uri.parse( "market://search?q=pname:" + packageName );
				Intent intent = new Intent( Intent.ACTION_VIEW, uri );
				activity.startActivity( intent );
			}

		} );

		setButton( BUTTON_NEGATIVE, activity.getString( android.R.string.no ), new OnClickListener() {

			public void onClick( DialogInterface dialogInterface, int i ) {
			}

		} );

	}

}
