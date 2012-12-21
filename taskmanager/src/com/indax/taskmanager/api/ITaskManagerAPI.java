package com.indax.taskmanager.api;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.indax.taskmanager.net.HttpEntityWithProgress.ProgressListener;

public interface ITaskManagerAPI {
	JSONObject account_login(String username, String password,
			ProgressListener progressListener) throws ClientProtocolException,
			IOException, JSONException;

	JSONObject task(String next, ProgressListener progressListener)
			throws ClientProtocolException, IOException, JSONException;

	JSONObject oplog(String next, ProgressListener progressListener)
			throws ClientProtocolException, IOException, JSONException;

	JSONObject executelog(String next, String task_guid,
			ProgressListener progressListener) throws ClientProtocolException,
			IOException, JSONException;

	JSONObject executelog_insert(String task_guid, String log_time,
			String remark, ProgressListener progressListener)
			throws ClientProtocolException, IOException, JSONException;

	JSONObject event(String next, String contact,
			ProgressListener progressListener) throws ClientProtocolException,
			IOException, JSONException;

	JSONObject event_insert(String datetime, String location, String persons,
			String event, ProgressListener progressListener)
			throws ClientProtocolException, IOException, JSONException;

	JSONObject contact(String name, ProgressListener progressListener)
			throws ClientProtocolException, IOException, JSONException;

	JSONObject contact_insert(String name, String name_en,
			ProgressListener progressListener) throws ClientProtocolException,
			IOException, JSONException;
}
