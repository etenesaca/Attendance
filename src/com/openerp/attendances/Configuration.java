package com.openerp.attendances;

import android.content.Context;
import android.content.SharedPreferences;

public class Configuration {
	// Definimos el nombre del archivo de configuracion
	private final String SHARED_PREFS_FILE = "AOPrefs";

	// Datos que se van a manejar
	private final String KEY_SERVER = "server";
	private final String KEY_PORT = "port";
	private final String KEY_DATABASE = "database";
	private final String KEY_LOGIN = "login";
	private final String KEY_PASSWORD = "pass";
	private final String KEY_PHOTO = "photo";

	// Datos del Perfil
	private final String KEY_NAME = "name";

	private Context mContext;

	public Configuration(Context context) {
		mContext = context;
	}

	// Obtenemos el archivo donde se guardan las preferencias para poder
	// modificarlas o leerlas
	private SharedPreferences getSettings() {
		return mContext.getSharedPreferences(SHARED_PREFS_FILE, 0);
	}

	/*
	 * SERVER
	 */
	public String getServer() {
		return getSettings().getString(KEY_SERVER, null);
	}

	public void setServer(String server) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_SERVER, server);
		editor.commit();
	}

	/*
	 * PORT
	 */
	public String getPort() {
		return getSettings().getString(KEY_PORT, null);
	}

	public void setPort(String port) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_PORT, port);
		editor.commit();
	}

	/*
	 * USERNAME
	 */
	public void setLogin(String login) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_LOGIN, login);
		editor.commit();
	}

	public String getLogin() {
		return getSettings().getString(KEY_LOGIN, null);
	}

	/*
	 * DATABASE
	 */
	public void setDataBase(String database) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_DATABASE, database);
		editor.commit();
	}

	public String getDataBase() {
		return getSettings().getString(KEY_DATABASE, null);
	}

	/*
	 * PASSWORD
	 */
	public String getPassword() {
		return getSettings().getString(KEY_PASSWORD, null);
	}

	public void setPassword(String pass) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_PASSWORD, pass);
		editor.commit();
	}

	/*
	 * NOMBRE DEL EMPLEADO
	 */
	public String getName() {
		return getSettings().getString(KEY_NAME, null);
	}

	public void setName(String name) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_NAME, name);
		editor.commit();
	}

	/*
	 * PHOTO DEL EMPLEADO
	 */
	public String getPhoto() {
		return getSettings().getString(KEY_PHOTO, null);
	}

	public void setPhoto(String Photo) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_PHOTO, Photo);
		editor.commit();
	}
}
