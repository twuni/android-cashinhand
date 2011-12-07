package org.twuni.money.wallet.activity;

import org.twuni.money.wallet.R;
import org.twuni.money.wallet.adapter.TreasuryViewAdapter;
import org.twuni.money.wallet.application.WalletApplication;
import org.twuni.money.wallet.application.WalletApplication.Extra;
import org.twuni.money.wallet.application.WalletApplication.Request;
import org.twuni.money.wallet.dialog.WithdrawDialog;
import org.twuni.money.wallet.task.ReloadTask;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

public class TreasuryListActivity extends ListActivity {

	private WalletApplication application;
	private TreasuryViewAdapter adapter;
	private String selectedTreasury;

	public void deposit( View view ) {
		application.receive( this );
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) {

		if( resultCode != RESULT_OK ) {
			super.onActivityResult( requestCode, resultCode, data );
			return;
		}

		switch( Request.valueOf( requestCode ) ) {

			case RECEIVE:
				application.deposit( this, data.getStringExtra( Intent.EXTRA_TEXT ) );
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

	@Override
	public boolean onContextItemSelected( MenuItem item ) {
		switch( item.getItemId() ) {
			case R.id.about:
				startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( selectedTreasury ) ) );
				return true;
			case R.id.withdraw:
				showDialog( WithdrawDialog.ID );
				return true;
			case R.id.abandon:
				application.deleteTreasury( selectedTreasury );
				selectedTreasury = null;
			case R.id.refresh:
				new ReloadTask( this, application, adapter ).execute();
				return true;
			default:
				return super.onContextItemSelected( item );
		}
	}

	@Override
	protected void onCreate( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_CUSTOM_TITLE );

		application = (WalletApplication) getApplication();
		setContentView( R.layout.treasury_list );
		adapter = new TreasuryViewAdapter( this );
		setListAdapter( adapter );
		registerForContextMenu( getListView() );

		getWindow().setFeatureInt( Window.FEATURE_CUSTOM_TITLE, R.layout.title );

	}

	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo ) {
		super.onCreateContextMenu( menu, v, menuInfo );
		selectTreasury( ( (AdapterContextMenuInfo) menuInfo ).position );
		getMenuInflater().inflate( R.menu.treasury, menu );
	}

	@Override
	protected Dialog onCreateDialog( int id ) {
		switch( id ) {
			case WithdrawDialog.ID:
				return new WithdrawDialog( this, application, selectedTreasury );
		}
		return super.onCreateDialog( id );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		getMenuInflater().inflate( R.menu.treasury_list, menu );
		return true;
	}

	@Override
	protected void onListItemClick( ListView l, View v, int position, long id ) {
		selectTreasury( position );
		showDialog( WithdrawDialog.ID );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		switch( item.getItemId() ) {
			case R.id.refresh:
				new ReloadTask( this, application, adapter ).execute();
				return true;
		}
		return super.onOptionsItemSelected( item );
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		if( intent != null && Intent.ACTION_MAIN.equals( intent.getAction() ) && intent.getCategories() != null && intent.getCategories().contains( Intent.CATEGORY_LAUNCHER ) ) {
			new ReloadTask( this, application, adapter ).execute();
		}
	}

	private void selectTreasury( int position ) {
		selectedTreasury = adapter.getViews().get( position ).getUrl();
	}

}
