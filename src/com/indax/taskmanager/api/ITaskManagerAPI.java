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
}
