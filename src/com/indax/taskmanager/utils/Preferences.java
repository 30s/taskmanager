package com.indax.taskmanager.utils;

import com.indax.taskmanager.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
	
	public static boolean DEBUG = false;
	
	public static void setLoginInfo(Context context, String username,
			String password, String token, String refresh_token, long expire) {
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

	public static long getExpire(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getLong(
				"expire", 0);
	}
	
	public static String getServer(Context context) {
		if ( Preferences.DEBUG ) {
			return context.getString(R.string.debug_server);			
		} else {
			return context.getString(R.string.production_server);
		}
	}
}
