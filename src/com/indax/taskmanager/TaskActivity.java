package com.indax.taskmanager;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

import com.indax.taskmanager.adapter.TaskListAdapter;
import com.indax.taskmanager.utils.Preferences;
import com.indax.taskmanager.utils.Utils;

public class TaskActivity extends Activity {

	private final String TAG = TaskActivity.class.getSimpleName();
	private TaskListAdapter task_adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task);

		ListView lst_task = (ListView) findViewById(R.id.lst_task);
		task_adapter = new TaskListAdapter();
		lst_task.setAdapter(task_adapter);
		
		new GetTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_task, menu);
		return true;
	}

	private class GetTask extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... params) {			
			return Utils.load_task_list(getApplicationContext(), 
					TaskActivity.this.task_adapter.getTaskList());			
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			
			try {
				String message = result.getString("message");
				Log.d(TAG, message);
				if ( message.equals("401") ) {
					String username = Preferences.getRememberedUsername(getBaseContext());
					String password = Preferences.getRememberedPassword(getBaseContext());
					
					if ( username.length() != 0 && password.length() != 0 ) {
						new LoginTask().execute(username, password);
					} else {
						startActivity(new Intent(getApplicationContext(),
								LoginActivity.class));											
					}			
				}
			} catch (JSONException e) {
				TaskActivity.this.task_adapter.notifyDataSetChanged();
			}
		}
	} // GetTask
	
	private class LoginTask extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... params) {
			return Utils.login(getApplicationContext(), params[0],
					params[1]);
		}
		
		@Override
		protected void onPostExecute(JSONObject ret) {		
			super.onPostExecute(ret);
			
			if (ret.has("token")) {
				new GetTask().execute();
			} else {
				startActivity(new Intent(getApplicationContext(), LoginActivity.class));
			}			
		}		
		
	} // LoginTask
}
