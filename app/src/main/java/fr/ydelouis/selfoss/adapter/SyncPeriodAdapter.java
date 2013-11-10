package fr.ydelouis.selfoss.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import fr.ydelouis.selfoss.sync.SyncPeriod;

public class SyncPeriodAdapter extends ArrayAdapter<String> {

	public SyncPeriodAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_1, SyncPeriod.getTexts(context));
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}
}
