package com.indax.taskmanager;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

import com.indax.taskmanager.adapter.ExecuteLogListAdapter;
import com.indax.taskmanager.api.ITaskManagerAPI;
import com.indax.taskmanager.api.TaskManagerAPI;
import com.indax.taskmanager.models.ExecuteLog;

public class ExecuteLogActivity extends Activity {

	private ITaskManagerAPI api_client;
	private String task_guid;
	private ExecuteLogListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_execute_log);

		api_client = TaskManagerAPI.getInstance(getApplicationContext());

		TextView txt_task = (TextView) findViewById(R.id.txt_task);

		ListView lst_exec_log = (ListView) findViewById(R.id.lst_exec_log);
		adapter = new ExecuteLogListAdapter();
		lst_exec_log.setAdapter(adapter);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			txt_task.setText(extras.getString("task_name"));
			task_guid = extras.getString("task_guid");
			new GetExecuteLogTask().execute(task_guid);
		}
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
			if ( result != null && result.has("objects") ) {
				JSONArray jExecLogs;
				try {
					jExecLogs = result.getJSONArray("objects");
					for ( int i = 0; i < jExecLogs.length(); i++ ) {
						ExecuteLog log = new ExecuteLog(jExecLogs.getJSONObject(i));
						adapter.addExecuteLog(log);
					}
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
