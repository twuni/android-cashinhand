package org.twuni.money.wallet.activity;

import org.twuni.money.wallet.R;
import org.twuni.money.wallet.application.WalletApplication;
import org.twuni.money.wallet.application.WalletApplication.Extra;
import org.twuni.money.wallet.application.WalletApplication.Request;
import org.twuni.money.wallet.dialog.WithdrawDialog;
import org.twuni.money.wallet.task.ReloadTask;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class TreasuryListActivity extends ListActivity {

	private WalletApplication application;
	private SimpleCursorAdapter adapter;
	private String selectedTreasury;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_CUSTOM_TITLE );

		application = (WalletApplication) getApplication();
		setContentView( R.layout.treasury_list );
		adapter = new SimpleCursorAdapter( this, R.layout.treasury_list_item, null, new String [] { "_id", "balance" }, new int [] { R.id.treasury, R.id.balance } );
		setListAdapter( adapter );

		getWindow().setFeatureInt( Window.FEATURE_CUSTOM_TITLE, R.layout.title );

	}

	@Override
	protected void onResume() {
		super.onResume();
		new ReloadTask( this, application, adapter ).execute();
	}

	@Override
	protected void onListItemClick( ListView l, View v, int position, long id ) {
		adapter.getCursor().moveToPosition( position );
		selectedTreasury = adapter.getCursor().getString( 0 );
		showDialog( WithdrawDialog.ID );
	}

	@Override
	protected Dialog onCreateDialog( int id, Bundle args ) {
		switch( id ) {
			case WithdrawDialog.ID:
				return new WithdrawDialog( this, application, selectedTreasury );
		}
		return super.onCreateDialog( id, args );
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) {

		if( resultCode != RESULT_OK ) {
			super.onActivityResult( requestCode, resultCode, data );
			return;
		}

		switch( Request.valueOf( requestCode ) ) {

			case SCAN:
				application.deposit( this, data.getStringExtra( Extra.SCAN_RESULT.toString() ) );
				break;

			case WITHDRAW:
				application.share( this, data.getStringExtra( Extra.TOKEN.toString() ) );
				break;

			case DEPOSIT:
			case SHARE:
				break;

			default:
				super.onActivityResult( requestCode, resultCode, data );

		}

	}

	public void deposit( View view ) {
		application.scan( this );
	}

}
