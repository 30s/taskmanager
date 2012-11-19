package com.indax.taskmanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
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
	private Utils m_utils;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		m_utils = new Utils(getBaseContext());
		
		if ( Preferences.getToken(getApplicationContext()) != null ) {
			startActivity(new Intent(getApplicationContext(), TaskActivity.class));
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
			if (params.length != 2) {
				return null;
			}

			String username = (String) params[0];
			String password = (String) params[1];
			Log.d(TAG, username + password);

			InputStream is = null;
			OutputStream os = null;
			int len = 500;
			try {
				String query = String.format(
						"username=%s&password=%s&apikey=%s", URLEncoder.encode(
								username, "utf-8"), URLEncoder.encode(password,
								"utf-8"), URLEncoder.encode(
								"d09f0e36753b2299c7cfd3d488b701", "utf-8"));

				URL url = new URL("http://192.168.1.123:8000/v1/account/login/");
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(10000 /* milliseconds */);
				conn.setConnectTimeout(15000 /* milliseconds */);
				conn.setRequestMethod("POST");
				conn.setDoInput(true);
				conn.setDoOutput(true);
				os = conn.getOutputStream();
				os.write(query.getBytes("utf-8"));

				conn.connect();
				int response = conn.getResponseCode();
				Log.d(TAG, "The response is: " + response);
				is = conn.getInputStream();
				String contentAsString = readIt(is, len);
				Log.d(TAG, contentAsString);
				JSONObject json = new JSONObject(contentAsString);				
				Preferences
						.setLoginInfo(getApplicationContext(),
								json.getString("token"),
								json.getString("refresh_token"),
								json.getLong("expire"));
				Log.d(TAG, "token: " + json.get("token"));
				conn.disconnect();
				return contentAsString;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return "";

		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

		// Reads an InputStream and converts it to a String.
		public String readIt(InputStream stream, int len) throws IOException,
				UnsupportedEncodingException {
			Reader reader = null;
			reader = new InputStreamReader(stream, "UTF-8");
			char[] buffer = new char[len];
			reader.read(buffer);
			return new String(buffer);
		}
	}
}