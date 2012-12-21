package com.indax.taskmanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.indax.taskmanager.R;
import com.indax.taskmanager.adapter.EventListAdapter;

public class EventFragment extends SherlockFragment {

	private EventListAdapter event_adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_events, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ListView lst_events = (ListView) getActivity().findViewById(R.id.lst_events);
		lst_events.setAdapter(event_adapter);
	}
	
	public void setEventAdapter(EventListAdapter adapter) {
		event_adapter = adapter;
	}
}
