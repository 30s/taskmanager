package com.indax.taskmanager.api;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.indax.taskmanager.R;
import com.indax.taskmanager.net.ApiBase;
import com.indax.taskmanager.net.ApiRequest;
import com.indax.taskmanager.net.ApiResponse;
import com.indax.taskmanager.net.HttpEntityWithProgress.ProgressListener;
import com.indax.taskmanager.utils.Preferences;
import com.indax.taskmanager.utils.Utils;

public class TaskManagerAPI extends ApiBase implements ITaskManagerAPI {

	private Context context;
	private static ITaskManagerAPI INSTANCE;

	public TaskManagerAPI(Context context) {
		super(context);
		this.context = context;
	}

	public static ITaskManagerAPI getInstance(Context context) {
		if (INSTANCE == null) {
			INSTANCE = new TaskManagerAPI(context);
		}
		return INSTANCE;
	}

	@Override
	public ApiResponse execute(ApiRequest request, ProgressListener listener)
			throws ClientProtocolException, IOException {
		if (!Utils.isNetworkAvailable(context)) {
			throw new IOException("Network not available!");
		}
		
		if (!request.getPath().equals("/v1/account/login/")) {
			request.addHeader("AUTHORIZATION",
					"Bearer " + Preferences.getToken(context));
		}
		
		ApiResponse response = super.execute(request, listener);		
		if (response.getStatusCode() == 401) {
			Preferences.expireToken(context);
		}
		
		return response;
	}

	@Override
	public JSONObject account_login(String username, String password,
			ProgressListener progressListener) throws ClientProtocolException,
			IOException, JSONException {
		ApiRequest request = new ApiRequest(ApiRequest.POST,
				"/v1/account/login/");
		request.addParameter("username", username);
		request.addParameter("password", password);
		request.addParameter("apikey", context.getString(R.string.apikey));

		ApiResponse response = execute(request, progressListener);

		return new JSONObject(response.getContentAsString());
	}

	@Override
	public JSONObject task(String next, ProgressListener progressListener)
			throws ClientProtocolException, IOException, JSONException {
		ApiRequest request = new ApiRequest(ApiRequest.GET,
				next == null ? "/v1/task/" : next);

		ApiResponse response = execute(request, progressListener);

		return new JSONObject(response.getContentAsString());
	}

	@Override
	public JSONObject oplog(String next, ProgressListener progressListener)
			throws ClientProtocolException, IOException, JSONException {
		ApiRequest request = new ApiRequest(ApiRequest.GET,
				next == null ? "/v1/oplog/" : next);
		request.addParameter("timestamp__gt", Preferences.getSyncTime(context)
				+ "");

		ApiResponse response = execute(request, progressListener);

		return new JSONObject(response.getContentAsString());
	}

	@Override
	public JSONObject executelog(String next, String task_guid,
			ProgressListener progressListener) throws ClientProtocolException,
			IOException, JSONException {
		ApiRequest request = new ApiRequest(ApiRequest.GET,
				next == null ? "/v1/executelog/" : next);
		request.addParameter("task__exact", task_guid);

		ApiResponse response = execute(request, progressListener);

		return new JSONObject(response.getContentAsString());
	}

	@Override
	public JSONObject executelog_insert(String task_guid, String log_time,
			String remark, ProgressListener progressListener)
			throws ClientProtocolException, IOException, JSONException {

		ApiRequest request = new ApiRequest(ApiRequest.POST,
				"/v1/executelog/insert/");
		request.addParameter("task", task_guid);
		request.addParameter("log_time", log_time);
		request.addParameter("remark", remark);

		ApiResponse response = execute(request, progressListener);

		return new JSONObject(response.getContentAsString());
	}

	@Override
	public JSONObject event(String next, String contact,
			ProgressListener progressListener) throws ClientProtocolException,
			IOException, JSONException {
		ApiRequest request = new ApiRequest(ApiRequest.GET,
				next == null ? "/v1/event/" : next);
		request.addParameter("persons__contains", contact);

		ApiResponse response = execute(request, progressListener);

		return new JSONObject(response.getContentAsString());
	}
}
