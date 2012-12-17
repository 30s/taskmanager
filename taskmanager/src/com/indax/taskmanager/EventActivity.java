package com.indax.taskmanager;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.Window;
import com.indax.taskmanager.api.ITaskManagerAPI;
import com.indax.taskmanager.api.TaskManagerAPI;

public class EventActivity extends SherlockFragmentActivity implements
		OnClickListener {

	private EditText edit_persons;
	private ITaskManagerAPI api_client;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_event);
		setSupportProgressBarIndeterminateVisibility(false);
		
		api_client = TaskManagerAPI.getInstance(getApplicationContext());

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			edit_persons = (EditText) findViewById(R.id.edit_persons);
			edit_persons.setText("我;" + extras.getString("contact"));
		}

		Button btn_save = (Button) findViewById(R.id.btn_save);
		btn_save.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_event, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_save) {
			DatePicker pk_date = (DatePicker) findViewById(R.id.pk_date);
			TimePicker pk_time = (TimePicker) findViewById(R.id.pk_time);
			EditText edit_location = (EditText) findViewById(R.id.edit_location);
			EditText edit_event = (EditText) findViewById(R.id.edit_event);

			String datetime = String.format("%02d-%02d-%02d %02d:%02d:%02d",
					pk_date.getYear(), pk_date.getMonth() + 1,
					pk_date.getDayOfMonth(), pk_time.getCurrentHour(),
					pk_time.getCurrentMinute(), 0);
			new LogEventTask().execute(datetime, edit_location.getText()
					.toString(), edit_persons.getText().toString(), edit_event
					.getText().toString());
		}
	}

	private class LogEventTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setSupportProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			JSONObject ret;
			try {
				ret = api_client.event_insert(params[0], params[1], params[2],
						params[3], null);
				if (ret.has("status")) {
					return true;
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			setSupportProgressBarIndeterminateVisibility(false);
			if (result) {
				Toast.makeText(getApplicationContext(), "Event post OK!",
						Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(getApplicationContext(), "Event post failed!",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
