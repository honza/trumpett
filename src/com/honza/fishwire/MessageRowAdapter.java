package com.honza.fishwire;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MessageRowAdapter extends BaseAdapter {
	
	private List<Message> elements;
	private Context mContext;

	public MessageRowAdapter(Context mContext, List<Message> elements) {
		this.mContext = mContext;
		this.elements = elements;

	}



	
	@Override
	public int getCount() {
		
		return elements.size();
	}

	@Override
	public Object getItem(int position) {
		
		return elements.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout rowLayout;
		final Message message = elements.get(position);
		
		if (convertView == null){
			rowLayout = (RelativeLayout)LayoutInflater.from(mContext).inflate(R.layout.row_message, parent, false);
		} else {
			rowLayout = (RelativeLayout)convertView;
		}
		
		TextView user = (TextView)rowLayout.findViewById(R.id.name);
		user.setText(message.user);
		
		TextView body = (TextView)rowLayout.findViewById(R.id.body);
		body.setText(message.body);
		
		return rowLayout;
		
		
	}

}
