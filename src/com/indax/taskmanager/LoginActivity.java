package com.indax.taskmanager;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.indax.taskmanager.utils.Preferences;
import com.indax.taskmanager.utils.Utils;

public class LoginActivity extends Activity implements OnClickListener {

	private final String TAG = LoginActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		Date now = new Date();
		long expire = Preferences.getExpire(getApplicationContext()) * 1000;

		if (Preferences.getToken(getApplicationContext()) != null
				&& now.getTime() < expire) {
			startActivity(new Intent(getApplicationContext(),
					TaskActivity.class));
			finish();
			return;
		}

		Button btn_login = (Button) findViewById(R.id.btn_login);
		btn_login.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_login) {
			if (!Utils.isNetworkAvailable(getBaseContext())) {
				Toast.makeText(getBaseContext(), R.string.hint_no_network,
						Toast.LENGTH_SHORT).show();
				return;
			}

			EditText edit_username = (EditText) findViewById(R.id.edit_username);
			EditText edit_password = (EditText) findViewById(R.id.edit_password);

			new LoginTask().execute(edit_username.getText().toString(),
					edit_password.getText().toString());
		}
	}

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
				startActivity(new Intent(getApplicationContext(),
						TaskActivity.class));
				LoginActivity.this.finish();
			} else {
				try {
					Toast.makeText(getApplicationContext(),
							ret.getString("message"), Toast.LENGTH_SHORT).show();
				} catch (JSONException e) {
					Toast.makeText(getApplicationContext(),
							"Login failed!", Toast.LENGTH_SHORT).show();
				}
			}			
		}
	}
}