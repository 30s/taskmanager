package com.indax.taskmanager.fragments;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.indax.taskmanager.EventActivity;
import com.indax.taskmanager.R;
import com.indax.taskmanager.adapter.EventListAdapter;
import com.indax.taskmanager.api.ITaskManagerAPI;
import com.indax.taskmanager.api.TaskManagerAPI;
import com.indax.taskmanager.models.Event;

public class EventFragment extends SherlockFragment {

	private EventListAdapter event_adapter;
	private String mContact;
	private ITaskManagerAPI api_client;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.fragment_events, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		api_client = TaskManagerAPI.getInstance(getActivity()
				.getApplicationContext());
		ListView lst_events = (ListView) getActivity().findViewById(R.id.lst_events);
		event_adapter = new EventListAdapter();
		lst_events.setAdapter(event_adapter);
		new GetEvent().execute(mContact);
	}
	
	public void setEventAdapter(EventListAdapter adapter) {
		event_adapter = adapter;
	}

	public void setContact(String name) {
		mContact = name;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_event, menu);
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_log:
			Intent intent = new Intent(getActivity().getApplicationContext(),
					EventActivity.class);
			intent.putExtra("contact", mContact);
			startActivity(intent);
			break;
		}		
		return super.onOptionsItemSelected(item);
	}
	
	private class GetEvent extends AsyncTask<String, LinearLayout, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject ret = null;
			try {
				if (params.length == 1) {
					ret = api_client.event(null, params[0], null);
				} else if (params.length == 2) {
					ret = api_client.event(params[0], params[1], null);
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return ret;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);

			if (result != null && result.has("objects")) {
				JSONArray jEvents;
				try {
					jEvents = result.getJSONArray("objects");
					for (int i = 0; i < jEvents.length(); i++) {
						Event event = new Event(jEvents.getJSONObject(i));
						event_adapter.addEvent(event);
					}
					event_adapter.notifyDataSetChanged();
					getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			// if (result != null && result.has("meta")) {
			// String next;
			// try {
			// next = result.getJSONObject("meta").getString("next");
			// if (!next.equals("null")) {
			// new GetEvent().execute(next, mContact);
			// }
			// } catch (JSONException e) {
			// e.printStackTrace();
			// }
			// }
		} // onPostExecutemContact
	} // GetEvent	
}
