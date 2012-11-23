package com.indax.taskmanager;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
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
		
		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(Tasks.CONTENT_URI, null, null, null, null);
		startManagingCursor(cursor);
		int idx_id   = cursor.getColumnIndex(Tasks.ID);
		int idx_name = cursor.getColumnIndex(Tasks.NAME);
		int idx_type = cursor.getColumnIndex(Tasks.TYPE);
		int idx_finish = cursor.getColumnIndex(Tasks.FINISH);
		int idx_remark = cursor.getColumnIndex(Tasks.REMARK);
		while ( cursor.moveToNext() ) {
			String name = cursor.getString(idx_name);
			String type = cursor.getString(idx_type);
			int finish  = cursor.getInt(idx_finish);
			String remark = cursor.getString(idx_remark);
			Task t = new Task(name, type.charAt(0), finish != 0, remark);
			task_expandable_adapter.addChild(t);
		}
		task_expandable_adapter.notifyDataSetChanged();
		
		if ( Preferences.getSyncTime(getApplicationContext()) == 0 ) {
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
				ContentResolver contentResolver = getContentResolver();
				Task task;
				for ( int i = 0; i < TaskActivity.this.tasks.size(); i++ ) {
					task = TaskActivity.this.tasks.get(i);
					ContentValues values = new ContentValues();
					values.put(Tasks.NAME, task.getName());
					values.put(Tasks.TYPE, task.getTypeAsString());
					values.put(Tasks.FINISH, task.getFinish());
					values.put(Tasks.REMARK, task.getRemark());
					contentResolver.insert(Tasks.CONTENT_URI, values);
				}
				Preferences.setSyncTime(getApplicationContext());
				task_expandable_adapter.notifyDataSetChanged();
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
