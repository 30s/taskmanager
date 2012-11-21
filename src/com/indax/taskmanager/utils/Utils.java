package com.indax.taskmanager.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// if no network is available networkInfo will be null
		// otherwise check if we are connected
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	public static String read(InputStream in) throws IOException,
			UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(
				new InputStreamReader(in, "UTF-8"), 1024);
		char[] buf = new char[1024];
		for (int read = r.read(buf); read != -1; read = r.read(buf)) {
			sb.append(buf, 0, read);
		}
		in.close();
		return sb.toString();
	}

	public static JSONObject login(Context context, String username,
			String password) {
		InputStream is = null;
		OutputStream os = null;

		HashMap<String, String> ret = new HashMap<String, String>();
		JSONObject json = null;
		ret.put("result", "failed");
		
		try {
			String query = String.format("username=%s&password=%s&apikey=%s",
					URLEncoder.encode(username, "utf-8"),
					URLEncoder.encode(password, "utf-8"),
					URLEncoder.encode("d09f0e36753b2299c7cfd3d488b701", "utf-8"));
			URL url = new URL(Preferences.getServer(context) + "/v1/account/login/");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			os = conn.getOutputStream();
			os.write(query.getBytes("utf-8"));

			conn.connect();
			is = conn.getInputStream();
			json = new JSONObject(Utils.read(is));
			if (json.has("token")) {
				Preferences.setLoginInfo(context, 
						json.getString("token"), json.getString("refresh_token"),
						json.getLong("expire"));
			}
			conn.disconnect();			
		} catch (UnsupportedEncodingException e) {
			ret.put("message", "Encoding error!");
		} catch (MalformedURLException e) {
			ret.put("message", "URL error!");
		} catch (IOException e) {
			ret.put("message", "Open connection error!");
		} catch (JSONException e) {
			ret.put("message", "Decode response error!");
		}

		if ( json == null ) {
			json = new JSONObject(ret);
		}
		return json;
	}
}
