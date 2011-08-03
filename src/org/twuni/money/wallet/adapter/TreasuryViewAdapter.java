package org.twuni.money.wallet.adapter;

import java.util.ArrayList;
import java.util.List;

import org.twuni.money.wallet.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TreasuryViewAdapter extends BaseAdapter {

	private final Context context;
	private List<TreasuryView> views;

	public TreasuryViewAdapter( Context context ) {
		this.context = context;
		this.views = new ArrayList<TreasuryView>();
	}

	public List<TreasuryView> getViews() {
		return views;
	}

	public void update( List<TreasuryView> views ) {
		this.views = views;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public Object getItem( int position ) {
		return views.get( position );
	}

	@Override
	public long getItemId( int position ) {
		return views.get( position ).getUrl().hashCode();
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {

		RelativeLayout layout = (RelativeLayout) View.inflate( context, R.layout.treasury_list_item, parent );
		TextView balance = (TextView) layout.findViewById( R.id.balance );
		TextView url = (TextView) layout.findViewById( R.id.treasury );

		TreasuryView treasuryView = (TreasuryView) getItem( position );

		balance.setText( treasuryView.getBalance() );
		url.setText( treasuryView.getUrl() );

		return layout;

	}

}
