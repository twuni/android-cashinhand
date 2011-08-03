package org.twuni.money.wallet.task;

import java.util.List;

import org.twuni.money.common.exception.ManyExceptions;
import org.twuni.money.wallet.R;
import org.twuni.money.wallet.adapter.TreasuryView;
import org.twuni.money.wallet.adapter.TreasuryViewAdapter;
import org.twuni.money.wallet.application.WalletApplication;
import org.twuni.money.wallet.util.DebugUtils;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;

public class ReloadTask extends AsyncTask<Void, ManyExceptions, List<TreasuryView>> {

	private final Activity activity;
	private final WalletApplication application;
	private final TreasuryViewAdapter adapter;

	public ReloadTask( Activity activity, WalletApplication application, TreasuryViewAdapter adapter ) {
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
	protected List<TreasuryView> doInBackground( Void... unused ) {
		try {
			application.checkIntegrity();
		} catch( ManyExceptions exceptions ) {
			publishProgress( exceptions );
		}
		return application.getBalance();
	}

	@Override
	protected void onProgressUpdate( ManyExceptions... exceptions ) {
		DebugUtils.handleException( activity, exceptions[0] );
	};

	@Override
	protected void onPostExecute( List<TreasuryView> views ) {
		adapter.update( views );
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
