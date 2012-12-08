package com.indax.taskmanager;

import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

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
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.indax.taskmanager.adapter.ExecuteLogListAdapter;
import com.indax.taskmanager.api.ITaskManagerAPI;
import com.indax.taskmanager.api.TaskManagerAPI;
import com.indax.taskmanager.models.ExecuteLog;
import com.indax.taskmanager.models.ExecuteLog.ExecuteLogs;
import com.indax.taskmanager.utils.Utils;

@TargetApi(11)
public class ExecuteLogActivity extends Activity implements OnClickListener,
		LoaderCallbacks<Cursor> {

	private static final int CACHED_LOG_LOADER = 0;
	private ITaskManagerAPI api_client;
	private String task_guid;
	private ExecuteLogListAdapter net_adapter;
	private ExecuteLogListAdapter cached_adapter;
	private EditText edit_log;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_execute_log);

		api_client = TaskManagerAPI.getInstance(getApplicationContext());

		TextView txt_task = (TextView) findViewById(R.id.txt_task);

		ListView lst_cached_log = (ListView) findViewById(R.id.lst_cached_log);
		cached_adapter = new ExecuteLogListAdapter();
		lst_cached_log.setAdapter(cached_adapter);

		ListView lst_exec_log = (ListView) findViewById(R.id.lst_exec_log);
		net_adapter = new ExecuteLogListAdapter();
		lst_exec_log.setAdapter(net_adapter);

		edit_log = (EditText) findViewById(R.id.edit_log);

		Button btn_add_log = (Button) findViewById(R.id.btn_add_log);
		btn_add_log.setOnClickListener(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			txt_task.setText(extras.getString("task_name"));
			task_guid = extras.getString("task_guid");
			new GetExecuteLogTask().execute(task_guid);
		}

		getLoaderManager().initLoader(CACHED_LOG_LOADER, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_execute_log, menu);
		return true;
	}

	private class GetExecuteLogTask extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... params) {
			try {
				if (params.length == 1) {
					return api_client.executelog(null, task_guid, null);
				} else {
					return api_client.executelog(params[0], task_guid, null);
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
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			if (result != null && result.has("objects")) {
				JSONArray jExecLogs;
				try {
					jExecLogs = result.getJSONArray("objects");
					for (int i = 0; i < jExecLogs.length(); i++) {
						ExecuteLog log = new ExecuteLog(
								jExecLogs.getJSONObject(i));
						net_adapter.addExecuteLog(log);
					}
					net_adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class PostOrCacheLogTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			assert (params.length == 2);
			String log_time = params[0];
			String remark = params[1];
			try {
				if (Utils.isNetworkAvailable(getApplicationContext())) {
					JSONObject ret = api_client.executelog_insert(task_guid,
							log_time, remark, null);
					if (ret.has("status")) {
						return true;
					}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			ContentResolver contentResolver = getContentResolver();
			ContentValues values = new ContentValues();
			values.put(ExecuteLogs.TASK, Integer.parseInt(task_guid));
			values.put(ExecuteLogs.LOG_TIME, Long.parseLong(log_time));
			values.put(ExecuteLogs.REMARK, remark);
			contentResolver.insert(ExecuteLogs.CONTENT_URI, values);

			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				Toast.makeText(getApplicationContext(), "Execute log post OK!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "Execute log saved!",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_add_log) {
			TimeZone tz = TimeZone.getDefault();
			Date now = new Date();
			int offset = tz.getOffset(now.getTime());
			long log_time = (now.getTime() - offset) / 1000;
			String remark = edit_log.getText().toString();

			new PostOrCacheLogTask().execute(log_time + "", remark);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (id == CACHED_LOG_LOADER) {
			String[] projections = new String[] { ExecuteLogs.ID,
					ExecuteLogs.TASK, ExecuteLogs.LOG_TIME, ExecuteLogs.REMARK };
			return new CursorLoader(this, ExecuteLogs.CONTENT_URI, projections,
					ExecuteLogs.TASK + " = " + task_guid, null,
					ExecuteLogs.LOG_TIME);
		}

		throw new IllegalArgumentException("Unknown loader id!");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		cached_adapter.load_cursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		cached_adapter.load_cursor(null);
	}
}
