package com.indax.taskmanager;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indax.taskmanager.adapter.TaskExpandableListAdapter;
import com.indax.taskmanager.api.ITaskManagerAPI;
import com.indax.taskmanager.api.TaskManagerAPI;
import com.indax.taskmanager.models.Task;
import com.indax.taskmanager.models.Task.Tasks;
import com.indax.taskmanager.utils.Preferences;

@TargetApi(11)
public class TaskActivity extends Activity implements LoaderCallbacks<Cursor>, OnChildClickListener {

	// private final String TAG = TaskActivity.class.getSimpleName();
	private TaskExpandableListAdapter task_adapter;
	private static int TASK_LOADER = 0;
	private ITaskManagerAPI api_client;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task);

		api_client = TaskManagerAPI.getInstance(getApplicationContext());

		ExpandableListView lst_task = (ExpandableListView) findViewById(R.id.lst_task);
		task_adapter = new TaskExpandableListAdapter();
		lst_task.setAdapter(task_adapter);
		lst_task.setOnChildClickListener(this);

		getLoaderManager().initLoader(TASK_LOADER, null, this);

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
		getMenuInflater().inflate(R.menu.activity_task, menu);
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
			findViewById(R.id.ll_progress).setVisibility(View.VISIBLE);
			TextView txt_loading = (TextView) findViewById(R.id.txt_loading);
			txt_loading.setText("Loading...");
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
					findViewById(R.id.ll_progress).setVisibility(View.GONE);
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
			findViewById(R.id.ll_progress).setVisibility(View.VISIBLE);
			TextView txt_loading = (TextView) findViewById(R.id.txt_loading);
			txt_loading.setText("Syncing...");
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
			findViewById(R.id.ll_progress).setVisibility(View.GONE);
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
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {	
		Task task = task_adapter.getChild(groupPosition, childPosition);
		Intent intent = new Intent(getApplicationContext(), ExecuteLogActivity.class);
		intent.putExtra("task_name", task.getName());
		intent.putExtra("task_guid", task.getGuid() + "");
		startActivity(intent);
		
		return false;
	}
}
