package com.indax.taskmanager;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.LinearLayout;

import com.actionbarsherlock.ActionBarSherlock.OnMenuItemSelectedListener;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.indax.taskmanager.adapter.EventListAdapter;
import com.indax.taskmanager.api.ITaskManagerAPI;
import com.indax.taskmanager.api.TaskManagerAPI;
import com.indax.taskmanager.fragments.ContactFragment;
import com.indax.taskmanager.fragments.EventFragment;
import com.indax.taskmanager.models.Event;

public class ContactActivity extends SherlockFragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor>, OnMenuItemSelectedListener,
		TabListener {

	private static final int CONTACT_LOADER = 0;
	private Uri mContactURL;
	private EventListAdapter event_adapter;
	private ITaskManagerAPI api_client;
	private String mContact;
	private final String[] tabs = { "Contact", "Events" };
	private ContactFragment mFragContact;
	private EventFragment mFragEvent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_contact);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		for (int i = 0; i < tabs.length; i++) {
			ActionBar.Tab tab = getSupportActionBar().newTab();
			tab.setText(tabs[i]);
			tab.setTabListener(this);
			getSupportActionBar().addTab(tab);
		}

		api_client = TaskManagerAPI.getInstance(getApplicationContext());
		event_adapter = new EventListAdapter();
		mContactURL = getIntent().getData();

		getSupportLoaderManager().initLoader(CONTACT_LOADER, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_contact, menu);
		return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
		if (id == CONTACT_LOADER) {
			return new CursorLoader(this, mContactURL, null, null, null, null);
		}

		throw new IllegalArgumentException("Unknown loader id!");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursor.moveToNext()) {
			int idx_display_name = cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			String display_name = cursor.getString(idx_display_name);
			mContact = display_name;
			if (mFragContact != null) {
				mFragContact.setContact(mContact);
			}
			new GetEvent().execute(mContact);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
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
			setSupportProgressBarIndeterminateVisibility(true);
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
					setSupportProgressBarIndeterminateVisibility(false);
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

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_log:
			Intent intent = new Intent(getApplicationContext(),
					EventActivity.class);
			intent.putExtra("contact", mContact);
			startActivity(intent);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (tab.getText().equals("Contact")) {
			if (mFragContact == null) {
				mFragContact = new ContactFragment();
			}
			ft.replace(R.id.frag_container, mFragContact);
		} else if (tab.getText().equals("Events")) {
			if (mFragEvent == null) {
				mFragEvent = new EventFragment();
				mFragEvent.setEventAdapter(event_adapter);
			}
			ft.replace(R.id.frag_container, mFragEvent);
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}
}
