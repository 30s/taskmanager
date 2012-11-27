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
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.indax.taskmanager.models.Task;

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

	public static JSONObject login(Context context, String username,
			String password) {
		InputStream is = null;
		OutputStream os = null;
		HttpURLConnection conn = null;

		HashMap<String, String> ret = new HashMap<String, String>();
		JSONObject json = null;
		ret.put("result", "failed");

		try {
			String query = String.format("username=%s&password=%s&apikey=%s",
					URLEncoder.encode(username, "utf-8"), URLEncoder.encode(
							password, "utf-8"), URLEncoder.encode(
							"d09f0e36753b2299c7cfd3d488b701", "utf-8"));
			URL url = new URL(Preferences.getServer(context)
					+ "/v1/account/login/");
			conn = (HttpURLConnection) url.openConnection();
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
				Preferences
						.setLoginInfo(context, json.getString("token"),
								json.getString("refresh_token"),
								json.getLong("expire"));
			} else {
				Preferences.expireToken(context);
			}
		} catch (UnsupportedEncodingException e) {
			ret.put("message", "Encoding error!");
		} catch (MalformedURLException e) {
			ret.put("message", "URL error!");
		} catch (IOException e) {
			ret.put("message", "Open connection error!");
		} catch (JSONException e) {
			ret.put("message", "Decode response error!");
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		if (json == null) {
			json = new JSONObject(ret);
		}
		return json;
	}

	public static JSONObject load_task_list(Context context,
			ArrayList<Task> tasks) {
		HashMap<String, String> ret = new HashMap<String, String>();

		InputStream is = null;
		HttpURLConnection conn = null;
		String path = "/v1/task/?format=json";

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
					JSONObject json_task = (JSONObject) json_array.get(i);
					tasks.add(new Task(json_task.getInt("id"), json_task
							.getString("name"), json_task.getString("type")
							.charAt(0), json_task.getBoolean("finish"),
							json_task.getString("remark")));
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
