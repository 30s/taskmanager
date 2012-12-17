package com.indax.taskmanager.adapter;

import java.util.ArrayList;

import com.indax.taskmanager.R;
import com.indax.taskmanager.models.Event;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EventListAdapter extends BaseAdapter {

	private ArrayList<Event> mEvents;
	
	public EventListAdapter() {
		this.mEvents = new ArrayList<Event>();
	}
	
	@Override
	public int getCount() {
		return mEvents.size();
	}

	@Override
	public Object getItem(int position) {
		return mEvents.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.item_event, null);
		}
		
		Event event = (Event) getItem(position);
		TextView txt_datetime = (TextView) convertView.findViewById(R.id.txt_datetime);
		TextView txt_location = (TextView) convertView.findViewById(R.id.txt_location);
		TextView txt_persons = (TextView) convertView.findViewById(R.id.txt_persons);
		TextView txt_event = (TextView) convertView.findViewById(R.id.txt_event);
		txt_datetime.setText(event.getDatetime());
		txt_location.setText(event.getLocation());
		txt_persons.setText(event.getPersons());
		txt_event.setText(Html.fromHtml(event.getEvent()));
		
		return convertView;
	}

	public void addEvent(Event event) {
		mEvents.add(event);
	}

}
