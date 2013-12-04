package com.openerp.attendances.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.openerp.attendances.Configuration;
import com.openerp.attendances.OpenErpConnect;
import com.openerp.attendances.R;

public class RegisterActivity extends Activity implements OnClickListener {
	private Configuration config;
	private QuickContactBadge pctFoto;
	private Button btnRefresh;
	private Button btnRegisterAttendance;
	private TextView txtNombre;
	private LinearLayout contenedor_error_connection;
	private LinearLayout contenedor_register;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_);

		// Crear una instancia de la Clase de Configuraciones
		config = new Configuration(this);

		pctFoto = (QuickContactBadge) findViewById(R.id.pctFoto);
		pctFoto.setOnClickListener(this);

		btnRefresh = (Button) findViewById(R.id.btnRefresh);
		btnRefresh.setOnClickListener(this);

		btnRegisterAttendance = (Button) findViewById(R.id.btnRegisterAttendance);
		btnRegisterAttendance.setOnClickListener(this);

		txtNombre = (TextView) findViewById(R.id.txtName);

		// Cargar el Nombre de Usuario
		txtNombre.setText(config.getName());

		if (config.getPhoto() != null) {
			// Cargar la Foto
			byte[] photo = Base64.decode(config.getPhoto(), Base64.DEFAULT);
			Bitmap bmp = BitmapFactory.decodeByteArray(photo, 0, photo.length);
			pctFoto.setImageBitmap(bmp);
		}

		contenedor_error_connection = (LinearLayout) findViewById(R.id.contenedor_error_connection);
		contenedor_register = (LinearLayout) findViewById(R.id.contenedor_register);
		refresh_connection();
	}

	void refresh_connection() {
		// Verificar si hat conexion
		if (config.getServer() != null && config.getPort() != null) {
			if (OpenErpConnect.TestConnection(config.getServer(), Integer.parseInt(config.getPort()))) {
				contenedor_error_connection.setVisibility(View.INVISIBLE);
				contenedor_register.setVisibility(View.VISIBLE);

				// Verificar si debe registrar una entrada o una salida
				btnRegisterAttendance.setText("Registrar Entrada");

			} else {
				contenedor_error_connection.setVisibility(View.VISIBLE);
				contenedor_register.setVisibility(View.INVISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnRegisterAttendance:
			// TODO
			break;

		case R.id.btnRefresh:
			refresh_connection();
			break;

		case R.id.pctFoto:
			// Ir al perfil del Usuario
			Intent profile_act = new Intent(this, ProfileActivity.class);
			startActivity(profile_act);
			break;

		default:
			break;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_register, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mnSearchRegisters:
			// Para ir a la ventana se Configuraciones
			Intent search_act = new Intent(RegisterActivity.this, SearchActivity.class);
			startActivity(search_act);
			break;

		case R.id.mnSettings:
			// Para ir a la ventana se Configuraciones
			Intent config_act = new Intent(RegisterActivity.this, ConfigActivity.class);
			startActivity(config_act);
			break;

		case R.id.mnExit:
			// Para cerrara la aplicacion
			finish();
			break;

		default:
			break;
		}
		return true;
	}
}
