package org.twuni.money.wallet.activity;

import org.twuni.money.wallet.R;
import org.twuni.money.wallet.application.WalletApplication;
import org.twuni.money.wallet.application.WalletApplication.Action;
import org.twuni.money.wallet.application.WalletApplication.Extra;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class TreasuryListActivity extends ListActivity {

	private static final String TREASURY_BALANCE_QUERY = "SELECT treasury AS _id, SUM(value) AS balance FROM token GROUP BY treasury ORDER BY balance DESC";
	private static final String [] TREASURY_BALANCE_FIELDS = new String [] { "_id", "balance" };

	private static final int DEPOSIT_REQUEST = 6340517;
	private static final int SCAN_REQUEST = 5684;
	private static final int WITHDRAW_DIALOG = 101010101;

	private WalletApplication application;
	private SimpleCursorAdapter adapter;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );

		application = (WalletApplication) getApplication();

		requestWindowFeature( Window.FEATURE_CUSTOM_TITLE );

		adapter = new SimpleCursorAdapter( this, R.layout.treasury_list_item, null, TREASURY_BALANCE_FIELDS, new int [] { R.id.treasury, R.id.balance } );
		setListAdapter( adapter );

		getWindow().setFeatureInt( Window.FEATURE_CUSTOM_TITLE, R.layout.title );

		refreshList();

	}

	public void deposit( View view ) {
		Intent intent = new Intent( "com.google.zxing.client.android.SCAN" );
		intent.putExtra( "SCAN_MODE", "QR_CODE_MODE" );
		startActivityForResult( intent, SCAN_REQUEST );
	}

	public void withdraw( View view ) {
		Toast.makeText( this, view.getId(), Toast.LENGTH_SHORT ).show();
	}

	@Override
	protected Dialog onCreateDialog( int id, Bundle args ) {

		switch( id ) {
			case WITHDRAW_DIALOG:
				return createWithdrawDialog();
		}

		return super.onCreateDialog( id, args );

	}

	private Dialog createWithdrawDialog() {
		Dialog dialog = new Dialog( this );
		dialog.setContentView( R.layout.withdraw_dialog );
		dialog.setTitle( "Enter amount:" );
		return dialog;
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) {

		super.onActivityResult( requestCode, resultCode, data );

		switch( requestCode ) {
			case SCAN_REQUEST:
				if( resultCode == RESULT_OK ) {
					handleScanResponse( data );
				}
				break;
			case DEPOSIT_REQUEST:
				if( resultCode == RESULT_OK ) {
					handleDepositResponse( data );
				}
		}

	}

	private void handleScanResponse( Intent data ) {
		Intent intent = new Intent( Action.DEPOSIT.toString() );
		intent.putExtra( Extra.TOKEN.toString(), data.getStringExtra( "SCAN_RESULT" ) );
		startActivityForResult( intent, DEPOSIT_REQUEST );
	}

	private void handleDepositResponse( Intent data ) {
		refreshList();
	}

	private void refreshList() {

		new AsyncTask<Void, Void, Cursor>() {

			@Override
			protected Cursor doInBackground( Void... unused ) {
				return application.executeQuery( TREASURY_BALANCE_QUERY, new String [0] );
			}

			protected void onPostExecute( Cursor cursor ) {
				adapter.changeCursor( cursor );
			}

		}.execute( new Void [0] );

	}

}
