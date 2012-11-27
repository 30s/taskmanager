package com.indax.taskmanager;

import java.util.ArrayList;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.indax.taskmanager.adapter.TaskExpandableListAdapter;
import com.indax.taskmanager.models.Task;
import com.indax.taskmanager.models.Task.Tasks;
import com.indax.taskmanager.utils.Preferences;
import com.indax.taskmanager.utils.Utils;

@TargetApi(11)
public class TaskActivity extends Activity implements LoaderCallbacks<Cursor> {

	private final String TAG = TaskActivity.class.getSimpleName();
	private ArrayList<Task> tasks;
	private TaskExpandableListAdapter task_adapter;
	private static int TASK_LOADER = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task);

		tasks = new ArrayList<Task>(20);
		ExpandableListView lst_task = (ExpandableListView) findViewById(R.id.lst_task);
		task_adapter = new TaskExpandableListAdapter();
		lst_task.setAdapter(task_adapter);

		getLoaderManager().initLoader(TASK_LOADER, null, this);

		if (Preferences.getSyncTime(getApplicationContext()) == 0) {
			// get all tasks
			new GetTask().execute();
		} else {
			// sync
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_task, menu);
		return true;
	}

	private class GetTask extends AsyncTask<Void, LinearLayout, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... params) {
			return Utils.load_task_list(getApplicationContext(),
					TaskActivity.this.tasks);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			findViewById(R.id.ll_progress).setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);

			try {
				String message = result.getString("message");
				Log.d(TAG, message);
				if (message.equals("401")) {
					String username = Preferences
							.getRememberedUsername(getBaseContext());
					String password = Preferences
							.getRememberedPassword(getBaseContext());

					if (username.length() != 0 && password.length() != 0) {
						new LoginTask().execute(username, password);
					} else {
						startActivity(new Intent(getApplicationContext(),
								LoginActivity.class));
					}
				}
			} catch (JSONException e) {
				ContentResolver contentResolver = getContentResolver();
				Task task;
				for (int i = 0; i < TaskActivity.this.tasks.size(); i++) {
					task = TaskActivity.this.tasks.get(i);
					ContentValues values = new ContentValues();
					values.put(Tasks.GUID, task.getGuid());
					values.put(Tasks.NAME, task.getName());
					values.put(Tasks.TYPE, task.getTypeAsString());
					values.put(Tasks.FINISH, task.getFinish());
					values.put(Tasks.REMARK, task.getRemark());
					contentResolver.insert(Tasks.CONTENT_URI, values);
				}
				Preferences.setSyncTime(getApplicationContext());
			}
			findViewById(R.id.ll_progress).setVisibility(View.GONE);
		}
	} // GetTask

	private class SyncTask extends AsyncTask<Void, LinearLayout, JSONException> {

		@Override
		protected JSONException doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	private class LoginTask extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... params) {
			return Utils.login(getApplicationContext(), params[0], params[1]);
		}

		@Override
		protected void onPostExecute(JSONObject ret) {
			super.onPostExecute(ret);

			if (ret.has("token")) {
				new GetTask().execute();
			} else {
				Preferences.expireToken(getApplicationContext());
				startActivity(new Intent(getApplicationContext(),
						LoginActivity.class));
				TaskActivity.this.finish();
			}
		}

	} // LoginTask

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
}
