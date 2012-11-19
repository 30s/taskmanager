package com.indax.taskmanager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

import com.indax.taskmanager.adapter.TaskListAdapter;
import com.indax.taskmanager.models.Task;
import com.indax.taskmanager.utils.Preferences;
import com.indax.taskmanager.utils.Utils;

public class TaskActivity extends Activity {

	private final String TAG = TaskActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task);

		new GetTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_task, menu);
		return true;
	}

	private class GetTask extends AsyncTask<Void, Void, Task[]> {

		@Override
		protected Task[] doInBackground(Void... params) {
			InputStream is = null;
			Task[] tasks = null;
			try {
				URL url;
				url = new URL(Preferences.getServer(getApplicationContext()) + "/v1/task/?format=json");
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(10000 /* milliseconds */);
				conn.setConnectTimeout(15000 /* milliseconds */);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				conn.setRequestProperty("AUTHORIZATION", "Bearer "
						+ Preferences.getToken(getApplicationContext()));
				conn.connect();
				int response = conn.getResponseCode();
				Log.d(TAG, "The response is: " + response);
				is = conn.getInputStream();
				String contentAsString = Utils.read(is);
				Log.d(TAG, contentAsString);
				JSONObject json = new JSONObject(contentAsString);
				if ( json.has("objects") ) {
					JSONArray json_array = json.getJSONArray("objects");
					tasks = new Task[json_array.length()];					
					for ( int i = 0; i < json_array.length(); i++ ) {
						JSONObject json_task = (JSONObject) json_array.get(i);
						tasks[i] = new Task(json_task.getString("name"), 
								json_task.getString("type").charAt(0),
								json_task.getBoolean("finish"), 
								json_task.getString("remark"));
					}
				}
				
				return tasks;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Task[] result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			ListView lst_task = (ListView) findViewById(R.id.lst_task);
			TaskListAdapter task_adapter = new TaskListAdapter(result);
			lst_task.setAdapter(task_adapter);
		}
	}
}
