package com.indax.taskmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
	public static void setLoginInfo(Context context, String token,
			String refresh_token, long expire) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		preferences.edit().putString("token", token)
				.putString("refresh_token", refresh_token)
				.putLong("expire", expire).commit();
	}

	public static String getToken(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString("token", null);
	}
}
