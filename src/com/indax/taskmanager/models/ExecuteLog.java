package com.indax.taskmanager.models;

import org.json.JSONException;
import org.json.JSONObject;

public class ExecuteLog {
	private String logTime;
	private String remark;
	
	public ExecuteLog() {
		
	}

	public ExecuteLog(JSONObject jsonObject) {
		try {
			setLogTime(jsonObject.getString("log_time"));
			setRemark(jsonObject.getString("remark"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
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
}
