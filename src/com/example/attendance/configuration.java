package com.example.attendance;

import android.content.Context;
import android.content.SharedPreferences;

public class configuration {
	// Definimos el nombre del archivo de configuracion
	private final String SHARED_PREFS_FILE = "AOPrefs";
	// Definimos las claves que haran referencia a los atributos de
	// configuración
	private final String KEY_SERVER = "server";
	private final String KEY_PORT = "port";
	private final String KEY_DATABASE = "database";
	private final String KEY_LOGIN = "login";
	private final String KEY_PASSWORD = "pass";
	private final String KEY_PHOTO = "photo";
	private final String KEY_PHOTO_SMALL = "photo_small";

	// Datos del Perfil
	private final String KEY_NAME = "name";

	private Context mContext;

	public configuration(Context context) {
		mContext = context;
	}

	// Obtenemos el archivo donde se guardan las preferencias para poder
	// modificarlas o leerlas
	private SharedPreferences getSettings() {
		return mContext.getSharedPreferences(SHARED_PREFS_FILE, 0);
	}

	// Este metodo devuelve el valor almacenado correspondiente a la KEY_LOGIN
	// en caso de no tener valor
	// devuelve null

	// Mediante este metodo se almacena el valor que pasamos correspondiente al
	// KEY_SERVER

	public String getServer() {
		return getSettings().getString(KEY_SERVER, null);
	}

	public void setServer(String server) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_SERVER, server);
		editor.commit();
	}

	// Mediante este metodo se almacena el valor que pasamos correspondiente al
	// KEY_PORT

	public String getPort() {
		return getSettings().getString(KEY_PORT, null);
	}

	public void setPort(String port) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_PORT, port);
		editor.commit();
	}

	// Mediante este metodo se almacena el valor que pasamos correspondiente al
	// KEY_LOGIN
	public void setLogin(String login) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_LOGIN, login);
		editor.commit();
	}

	public String getLogin() {
		return getSettings().getString(KEY_LOGIN, null);
	}

	// Mediante este metodo se almacena el valor que pasamos correspondiente al
	// KEY_DATABASE
	public void setDataBase(String database) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_DATABASE, database);
		editor.commit();
	}

	public String getDataBase() {
		return getSettings().getString(KEY_DATABASE, null);
	}

	// Mediante este metodo se almacena el valor que pasamos correspondiente al
	// KEY_PASSWORD
	public String getPassword() {
		return getSettings().getString(KEY_PASSWORD, null);
	}

	public void setPassword(String pass) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_PASSWORD, pass);
		editor.commit();
	}

	// Mediante este metodo se almacena el valor que pasamos correspondiente al
	// KEY_PASSWORD
	public String getName() {
		return getSettings().getString(KEY_NAME, null);
	}

	public void setName(String name) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_NAME, name);
		editor.commit();
	}

	// Mediante este metodo se almacena el valor que pasamos correspondiente al
	// KEY_PHOTO
	public String getPhoto() {
		return getSettings().getString(KEY_PHOTO, null);
	}

	public void setPhoto(String Photo) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_PHOTO, Photo);
		editor.commit();
	}
	
	// Mediante este metodo se almacena el valor que pasamos correspondiente al
	// KEY_PHOTO SMALL
	public String getPhoto_Small() {
		return getSettings().getString(KEY_PHOTO_SMALL, null);
	}

	public void setPhoto_Small(String Photo_Small) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_PHOTO_SMALL, Photo_Small);
		editor.commit();
	}
}
