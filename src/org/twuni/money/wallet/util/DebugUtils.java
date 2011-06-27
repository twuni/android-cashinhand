package org.twuni.money.wallet.util;

import android.content.Context;
import android.widget.Toast;

public class DebugUtils {

	public static void handleException( Context context, Exception exception ) {
		Toast.makeText( context, exception.getMessage(), Toast.LENGTH_LONG ).show();
	}

}
