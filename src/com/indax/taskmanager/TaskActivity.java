package com.indax.taskmanager;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.indax.taskmanager.adapter.TaskExpandableListAdapter;
import com.indax.taskmanager.models.Task;
import com.indax.taskmanager.models.TaskType;
import com.indax.taskmanager.utils.Preferences;
import com.indax.taskmanager.utils.Utils;

public class TaskActivity extends Activity {

	private final String TAG = TaskActivity.class.getSimpleName();
	private ArrayList<Task> tasks;
	private TaskExpandableListAdapter task_expandable_adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task);

		tasks = new ArrayList<Task>(20);
		ExpandableListView lst_task = (ExpandableListView) findViewById(R.id.lst_task);
		task_expandable_adapter = new TaskExpandableListAdapter();
		lst_task.setAdapter(task_expandable_adapter);				
		
		new GetTask().execute();
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
				Task task;
				int group_position = 0;
				for ( int i = 0; i < TaskActivity.this.tasks.size(); i++ ) {
					task = TaskActivity.this.tasks.get(i);
					
					if ( task.getType() == TaskType.DAILY ) {
						group_position = 0;
					} else if ( task.getType() == TaskType.WEEKLY ) {
						group_position = 1;
					} else if ( task.getType() == TaskType.MONTHLY ) {
						group_position = 2;
					} else if ( task.getType() == TaskType.YEARLY ) {
						group_position = 3;
					} else {
						group_position = 4;
					}
					TaskActivity.this.task_expandable_adapter.getGroup(group_position).add(task);
				}
				TaskActivity.this.task_expandable_adapter.notifyDataSetChanged();
			}
			findViewById(R.id.ll_progress).setVisibility(View.GONE);
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
				Preferences.expireToken(getApplicationContext());
				startActivity(new Intent(getApplicationContext(), LoginActivity.class));
				TaskActivity.this.finish();
			}			
		}		
		
	} // LoginTask
}
