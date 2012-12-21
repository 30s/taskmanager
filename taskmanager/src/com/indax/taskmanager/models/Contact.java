package com.indax.taskmanager.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Contact {
//	private String name;
	private String name_en;
	
	public Contact(String name, JSONObject jsonObject) {
//		this.name = name;
		try {
			this.name_en = jsonObject.getString("name_en");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getNameEn() {
		return name_en;
	}

	public void setNameEn(String name_en) {
		this.name_en = name_en;
	}	
}
