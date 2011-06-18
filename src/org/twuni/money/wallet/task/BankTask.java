package org.twuni.money.wallet.task;

import org.twuni.money.wallet.util.DebugUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

public abstract class BankTask<Result> extends AsyncTask<Intent, Exception, Result> {

	private final Activity activity;

	public BankTask( Activity activity ) {
		this.activity = activity;
	}

	@Override
	protected final Result doInBackground( Intent... intents ) {

		Intent intent = intents == null ? null : intents[0];

		if( intent == null ) {
			cancel( true );
			return null;
		}

		try {
			return handleIntent( intent );
		} catch( Exception exception ) {
			publishProgress( exception );
			cancel( true );
		}

		return null;

	}

	@Override
	protected final void onProgressUpdate( Exception... exceptions ) {
		DebugUtils.handleException( activity, exceptions[0] );
	}

	@Override
	protected final void onPostExecute( Result result ) {
		Intent data = new Intent();
		putExtras( data, result );
		activity.setResult( Activity.RESULT_OK, data );
		activity.finish();
	}

	@Override
	protected final void onCancelled() {
		activity.setResult( Activity.RESULT_CANCELED );
		activity.finish();
	}

	protected abstract Result handleIntent( Intent intent );

	protected abstract void putExtras( Intent data, Result result );

}
