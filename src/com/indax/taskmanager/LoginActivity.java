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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.indax.taskmanager.utils.Preferences;
import com.indax.taskmanager.utils.Utils;

public class LoginActivity extends Activity implements OnClickListener, OnCheckedChangeListener {

	private final String TAG = LoginActivity.class.getSimpleName();
	private boolean remember = false;
	
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

		String username = Preferences.getRememberedUsername(getBaseContext());
		String password = Preferences.getRememberedPassword(getBaseContext());
		if ( username.length() != 0 && password.length() != 0 ) {
			new LoginTask().execute(username, password);
		}
		
		Button btn_login = (Button) findViewById(R.id.btn_login);
		btn_login.setOnClickListener(this);
		
		CheckBox chk_remember  = (CheckBox) findViewById(R.id.chk_remember);
		chk_remember.setOnCheckedChangeListener(this);
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
			CheckBox chk_remember  = (CheckBox) findViewById(R.id.chk_remember);
			
			String username = edit_username.getText().toString();
			String password = edit_password.getText().toString();
			
			if ( chk_remember.isChecked() ) {
				Preferences.setRemember(getApplicationContext(), username, password);
			}
			
			new LoginTask().execute(username, password);
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

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if ( buttonView.getId() == R.id.chk_remember ) {
			if ( isChecked ) {
				remember = true;
			} else {
				remember = false;
				Preferences.setRemember(getApplicationContext(), "", "");
			}
		}
	}
}