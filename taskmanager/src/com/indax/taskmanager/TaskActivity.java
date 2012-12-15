package com.indax.taskmanager;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.indax.taskmanager.adapter.TaskExpandableListAdapter;
import com.indax.taskmanager.api.ITaskManagerAPI;
import com.indax.taskmanager.api.TaskManagerAPI;
import com.indax.taskmanager.fragments.AllTasksFragment;
import com.indax.taskmanager.fragments.RecentTasksFragment;
import com.indax.taskmanager.fragments.StarredTasksFragment;
import com.indax.taskmanager.models.Task;
import com.indax.taskmanager.models.Task.Tasks;
import com.indax.taskmanager.utils.Preferences;


public class TaskActivity extends SherlockFragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>, TabListener {
	// private final String TAG = TaskActivity.class.getSimpleName();
	private TaskExpandableListAdapter task_adapter;
	private static int TASK_LOADER = 0;
	private ITaskManagerAPI api_client;
	private StarredTasksFragment mStarred;
	private AllTasksFragment mAll;
	private RecentTasksFragment mRecent;
	private static String[] tabs = new String[] {"All", "Star", "Recent"};;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task);

		api_client = TaskManagerAPI.getInstance(getApplicationContext());
		task_adapter = new TaskExpandableListAdapter();
		
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS); 
        for (int i = 0; i < tabs.length; i++) {
            ActionBar.Tab tab = getSupportActionBar().newTab();
            tab.setText(tabs[i]);
            tab.setTabListener(this);
            getSupportActionBar().addTab(tab);
        }
		
		getSupportLoaderManager().initLoader(TASK_LOADER, null, this);
		
		if (Preferences.getSyncTime(getApplicationContext()) == 0) {
			// get all tasks
			new GetTask().execute();
		} else {
			// sync
			new SyncTask().execute();
		}				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_task, menu);
		return true;
	}

	private class GetTask extends AsyncTask<String, LinearLayout, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject ret = null;
			try {
				if (params.length == 0) {
					ret = api_client.task(null, null);
				} else {
					ret = api_client.task(params[0], null);
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
//			findViewById(R.id.ll_progress).setVisibility(View.VISIBLE);
//			TextView txt_loading = (TextView) findViewById(R.id.txt_loading);
//			txt_loading.setText("Loading...");
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);

			if (result != null && result.has("objects")) {
				ContentResolver contentResolver = getContentResolver();
				JSONArray jTasks;
				try {
					jTasks = result.getJSONArray("objects");
					for (int i = 0; i < jTasks.length(); i++) {
						Task task = new Task(jTasks.getJSONObject(i));
						ContentValues values = new ContentValues();
						values.put(Tasks.GUID, task.getGuid());
						values.put(Tasks.NAME, task.getName());
						values.put(Tasks.TYPE, task.getTypeAsString());
						values.put(Tasks.FINISH, task.getFinish());
						values.put(Tasks.REMARK, task.getRemark());
						contentResolver.insert(Tasks.CONTENT_URI, values);
					}
					Preferences.setSyncTime(getApplicationContext());
					// findViewById(R.id.ll_progress).setVisibility(View.GONE);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			if (result != null && result.has("meta")) {
				String next;
				try {
					next = result.getJSONObject("meta").getString("next");
					if (!next.equals("null")) {
						new GetTask().execute(next);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} // onPostExecute
	} // GetTask

	private class SyncTask extends AsyncTask<String, LinearLayout, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... params) {
			try {
				if (params.length == 0) {
					return api_client.oplog(null, null);
				} else {
					return api_client.oplog(params[0], null);
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			findViewById(R.id.ll_progress).setVisibility(View.VISIBLE);
//			TextView txt_loading = (TextView) findViewById(R.id.txt_loading);
//			txt_loading.setText("Syncing...");
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);

			if (result != null && result.has("objects")) {
				ContentResolver contentResolver = getContentResolver();
				try {
					JSONArray jOplogs = result.getJSONArray("objects");
					for (int i = 0; i < jOplogs.length(); i++) {
						JSONObject log = jOplogs.getJSONObject(i);
						switch (log.getInt("opcode")) {
						case 1:
							// add
							if (log.getString("model").equals(
									"ax003d.taskmanager.models.Task")) {
								Task task = new Task(new JSONObject(
										log.getString("data")));
								ContentValues values = new ContentValues();
								values.put(Tasks.GUID, task.getGuid());
								values.put(Tasks.NAME, task.getName());
								values.put(Tasks.TYPE, task.getTypeAsString());
								values.put(Tasks.FINISH, task.getFinish());
								values.put(Tasks.REMARK, task.getRemark());
								contentResolver.insert(Tasks.CONTENT_URI,
										values);
								Preferences.setSyncTime(
										getApplicationContext(),
										log.getLong("timestamp"));
							}
							break;
						case 2:
							// update
							if (log.getString("model").equals(
									"ax003d.taskmanager.models.Task")) {
								Task task = new Task(new JSONObject(
										log.getString("data")));
								ContentValues values = new ContentValues();
								values.put(Tasks.GUID, task.getGuid());
								values.put(Tasks.NAME, task.getName());
								values.put(Tasks.TYPE, task.getTypeAsString());
								values.put(Tasks.FINISH, task.getFinish());
								values.put(Tasks.REMARK, task.getRemark());
								contentResolver.update(Uri.withAppendedPath(
										Tasks.CONTENT_URI,
										"/guid/" + task.getGuid()), values,
										null, null);
								Preferences.setSyncTime(
										getApplicationContext(),
										log.getLong("timestamp"));
							}
							break;
						case 3:
							// delete
							if (log.getString("model").equals(
									"ax003d.taskmanager.models.Task")) {
								JSONObject ret = new JSONObject(
										log.getString("data"));
								contentResolver.delete(Uri.withAppendedPath(
										Tasks.CONTENT_URI,
										"/guid/" + ret.getInt("id")), null,
										null);
								Preferences.setSyncTime(
										getApplicationContext(),
										log.getLong("timestamp"));
							}
							break;
						default:
							break;
						} // endswitch
					} // endfor 
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			} // endif
			
			if ( result != null && result.has("meta") ) {
				String next;
				try {
					next = result.getJSONObject("meta").getString("next");
					if ( !next.equals("null") ) {
						new SyncTask().execute(next);
					}					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} // endif
			// findViewById(R.id.ll_progress).setVisibility(View.GONE);
		} // onPostExecute
	} // SyncTask

//	private class LoginTask extends AsyncTask<String, Void, JSONObject> {
//
//		@Override
//		protected JSONObject doInBackground(String... params) {
//			JSONObject ret = null;
//			try {
//				return api_client.account_login(params[0], params[1], null);
//			} catch (ClientProtocolException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			return ret;
//		}
//
//		@Override
//		protected void onPostExecute(JSONObject ret) {
//			super.onPostExecute(ret);
//
//			if (ret != null && ret.has("token")) {
//				new GetTask().execute();
//			} else {
//				Preferences.expireToken(getApplicationContext());
//				startActivity(new Intent(getApplicationContext(),
//						LoginActivity.class));
//				TaskActivity.this.finish();
//			}
//		}
//
//	} // LoginTask

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (id == TASK_LOADER) {
			String[] projections = new String[] { Tasks._ID, Tasks.GUID,
					Tasks.NAME, Tasks.TYPE, Tasks.FINISH, Tasks.REMARK };
			return new CursorLoader(this, Tasks.CONTENT_URI, projections, null,
					null, Tasks.TYPE);
		}

		throw new IllegalArgumentException("Unknown loader id!");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		task_adapter.load_tasks(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		task_adapter.load_tasks(null);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if ( tab.getText().equals("All") ) {
			if ( mAll == null ) {
				mAll = new AllTasksFragment();
				mAll.setTaskAdapter(task_adapter);
			}
			ft.replace(R.id.frag_container, mAll);
		} else if ( tab.getText().equals("Star") ) {
			if ( mStarred == null ) {
				mStarred = new StarredTasksFragment();	
			}
			ft.replace(R.id.frag_container, mStarred);
		} else if ( tab.getText().equals("Recent") ) {
			if ( mRecent == null ) {
				mRecent = new RecentTasksFragment();
			}
			ft.replace(R.id.frag_container, mRecent);
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
}
