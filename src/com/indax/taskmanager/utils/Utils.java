package com.indax.taskmanager.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

	// private static String TAG = Utils.class.getSimpleName();

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

	public static JSONObject sync_tasks(Context context, ArrayList<JSONObject> oplogs) {
		HashMap<String, String> ret = new HashMap<String, String>();

		InputStream is = null;
		HttpURLConnection conn = null;
		String path = "/v1/oplog/?format=json&timestamp__gt=" + Preferences.getSyncTime(context);

		while (!path.equals("null")) {
			try {
				URL url = new URL(Preferences.getServer(context) + path);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				conn.setRequestProperty("AUTHORIZATION", "Bearer "
						+ Preferences.getToken(context));
				conn.connect();

				try {
					int response = conn.getResponseCode();
					if (response != 200) {
						ret.put("message", response + "");
						return new JSONObject(ret);
					}
				} catch (IOException e) {
					// assume it's a 401
					ret.put("message", "401");
					return new JSONObject(ret);
				}

				is = conn.getInputStream();
				JSONObject json = new JSONObject(Utils.read(is));
				JSONObject meta = json.getJSONObject("meta");
				path = meta.getString("next");
				if (!json.has("objects")) {
					continue;
				}

				JSONArray json_array = json.getJSONArray("objects");
				for (int i = 0; i < json_array.length(); i++) {
					JSONObject oplog = (JSONObject) json_array.get(i);
					oplogs.add(oplog);
				} // for
			} catch (MalformedURLException e) {
				ret.put("message", "URL error!");
				path = "null";
			} catch (IOException e) {
				ret.put("message", "Open connection error!");
				path = "null";
			} catch (JSONException e) {
				ret.put("message", "JSON error!");
				path = "null";
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}
		} // while

		return new JSONObject(ret);
	}
}
