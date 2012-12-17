package com.indax.taskmanager.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Event {
	private String mDatetime;
	private String mPersons;
	private String mLocation;
	private String mEvent;
	
	public Event(JSONObject jsonObject) {
		try {
			this.mDatetime = jsonObject.getString("datetime");
			this.mPersons = jsonObject.getString("persons");
			this.mLocation = jsonObject.getString("location");
			this.mEvent = jsonObject.getString("event");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getDatetime() {
		return mDatetime;
	}

	public void setDatetime(String mDatetime) {
		this.mDatetime = mDatetime;
	}

	public String getPersons() {
		return mPersons;
	}

	public void setPersons(String mPersons) {
		this.mPersons = mPersons;
	}

	public String getLocation() {
		return mLocation;
	}

	public void setLocation(String mLocation) {
		this.mLocation = mLocation;
	}

	public String getEvent() {
		return mEvent;
	}

	public void setEvent(String mEvent) {
		this.mEvent = mEvent;
	}
}
