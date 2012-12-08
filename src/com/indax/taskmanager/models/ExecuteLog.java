package com.indax.taskmanager.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.provider.BaseColumns;

import com.indax.taskmanager.providers.TaskContentProvider;

public class ExecuteLog {
	private long id;
	private String logTime;
	private String remark;
	
	public ExecuteLog() {
		
	}

	public ExecuteLog(JSONObject jsonObject) {
		try {
			this.id = -1;
			setLogTime(jsonObject.getString("log_time"));
			setRemark(jsonObject.getString("remark"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public ExecuteLog(long id, String log_time, String remark) {
		this.id = id;
		setLogTime(log_time);
		setRemark(remark);
	}

	public static final class ExecuteLogs implements BaseColumns {
		private ExecuteLogs() {
		}

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ TaskContentProvider.AUTHORITY + "/executelogs");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.taskmanager.executelogs";
		public static final String ID = "_id";
		public static final String TASK = "task";
		public static final String LOG_TIME = "log_time";
		public static final String REMARK = "remark";
	}	

	public String getLogTime() {
		return logTime;
	}

	public void setLogTime(String logTime) {
		this.logTime = logTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public long getID() {
		return id;
	}
}
