package org.twuni.money.wallet.task;

import org.twuni.money.common.exception.ManyExceptions;
import org.twuni.money.wallet.R;
import org.twuni.money.wallet.application.WalletApplication;
import org.twuni.money.wallet.util.DebugUtils;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.View;
import android.widget.CursorAdapter;

public class ReloadTask extends AsyncTask<Void, ManyExceptions, Cursor> {

	private static final String TREASURY_BALANCE_QUERY = "SELECT treasury AS _id, '$' || SUM( ROUND( value / 100.00, 2 ) ) AS balance FROM token GROUP BY treasury ORDER BY balance DESC";

	private final Activity activity;
	private final WalletApplication application;
	private final CursorAdapter adapter;

	public ReloadTask( Activity activity, WalletApplication application, CursorAdapter adapter ) {
		this.activity = activity;
		this.application = application;
		this.adapter = adapter;
	}

	@Override
	protected void onPreExecute() {
		setVisibility( R.id.loading, View.VISIBLE );
		setVisibility( R.id.empty, View.GONE );
	}

	public void execute() {
		execute( new Void [0] );
	}

	@Override
	protected Cursor doInBackground( Void... unused ) {
		try {
			application.checkIntegrity();
		} catch( ManyExceptions exceptions ) {
			publishProgress( exceptions );
		}
		return application.executeQuery( TREASURY_BALANCE_QUERY, new String [0] );
	}

	@Override
	protected void onProgressUpdate( ManyExceptions... exceptions ) {
		DebugUtils.handleException( activity, exceptions[0] );
	};

	@Override
	protected void onPostExecute( Cursor cursor ) {
		adapter.changeCursor( cursor );
		setVisibility( R.id.loading, View.GONE );
		if( adapter.isEmpty() ) {
			setVisibility( android.R.id.list, View.GONE );
			setVisibility( R.id.empty, View.VISIBLE );
		} else {
			setVisibility( android.R.id.list, View.VISIBLE );
		}
	}

	private void setVisibility( int resourceId, int visibility ) {
		activity.findViewById( resourceId ).setVisibility( visibility );
	}

}
