package com.indax.taskmanager;

import java.io.IOException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.indax.taskmanager.utils.Utils;

public class LoginActivity extends Activity implements OnClickListener {

	private final String TAG = LoginActivity.class.getSimpleName();
	private Utils m_utils;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		Button btn_login = (Button) findViewById(R.id.btn_login);
		btn_login.setOnClickListener(this);

		m_utils = new Utils(getBaseContext());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_login) {
			if (!m_utils.isNetworkAvailable()) {
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

	private class LoginTask extends AsyncTask {

		@Override
		protected Object doInBackground(Object... params) {
			if ( params.length != 2 ) {
				return null;
			}
			String username = (String) params[0];
			String password = (String) params[1];
			Log.d(TAG, username + password);
			return "";
		}
		
		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}
}