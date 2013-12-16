package com.openerp.attendances.activities;

import java.net.MalformedURLException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.openerp.attendances.Configuration;
import com.openerp.attendances.OpenErpConnect;
import com.openerp.attendances.R;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class ProfileActivity extends Activity {
	private Configuration config;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		// Activar el Boton Home
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Crear una instancia de la Clase de Configuraciones
		config = new Configuration(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String Server = config.getServer();
		String database = config.getDataBase();
		String user = config.getLogin();
		String pass = config.getPassword();

		switch (item.getItemId()) {
		case R.id.mnProfile_refresh:
			// Actualizar los datos del perfil desde el sevidor
			if (Server != null && config.getPort() != null && config.getDataBase() != null && config.getUserID() != null) {
				// Verificar si hay conexion
				if (OpenErpConnect.TestConnection(config.getServer(), Integer.parseInt(config.getPort()))) {
					Integer port = Integer.parseInt(config.getPort());
					Integer uid = Integer.parseInt(config.getUserID());
					try {
						OpenErpConnect conn = new OpenErpConnect(Server, port, database, user, pass, uid);
						if (conn != null) {

						}
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			}
			break;
		case android.R.id.home:
			// Para cerrara las ventana se busquedas
			finish();
			break;
		default:
			break;
		}
		return true;
	}
}
