package com.example.attendance;

import android.content.Context;
import android.content.SharedPreferences;

public class configuration {
	private final String SHARED_PREFS_FILE = "HMPrefs";
	private final String KEY_EMAIL = "email";

	private final String KEY_SERVER = "";
	private final String KEY_PORT = "";
	private final String KEY_DATABASE = "";
	private final String KEY_USER = "";
	private final String KEY_PASS = "";

	private Context mContext;

	public configuration(Context context) {
		mContext = context;
	}

	private SharedPreferences getSettings() {
		return mContext.getSharedPreferences(SHARED_PREFS_FILE, 0);
	}

	public String getUserEmail() {
		return getSettings().getString(KEY_EMAIL, null);
	}

	public void setUserEmail(String email) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_EMAIL, email);
		editor.commit();
	}


	/**
	 * KEY_SERVER
	 */
	public String getKEY_SERVER() {
		return getSettings().getString(KEY_SERVER, null);
	}

	public void setKEY_SERVER(String server) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_SERVER, server);
		editor.commit();
	}

	/**
	 * KEY_PORT
	 */
	public String getKKEY_PORT() {
		return getSettings().getString(KEY_PORT, null);
	}

	public void setKEY_PORT(String host) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_PORT, host);
		editor.commit();
	}

	/**
	 * KEY_DATABASE
	 */
	public String getKEY_DATABASE() {
		return getSettings().getString(KEY_DATABASE, null);
	}

	public void setKEY_DATABASE(String db) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_DATABASE, db);
		editor.commit();
	}
	
	/**
	 * KEY_USER
	 */
	public String getKEY_USER() {
		return getSettings().getString(KEY_USER, null);
	}
	
	public void setKEY_USER(String user) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_USER, user);
		editor.commit();
	}
	
	/**
	 * KEY_PASS
	 */
	public String getKEY_PASS() {
		return getSettings().getString(KEY_PASS, null);
	}
	
	public void setKEY_PASS(String pass) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_PASS, pass);
		editor.commit();
	}
}
