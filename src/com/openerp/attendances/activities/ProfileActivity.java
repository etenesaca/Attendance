package com.openerp.attendances.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.openerp.attendances.Configuration;
import com.openerp.attendances.OpenErpConnect;
import com.openerp.attendances.R;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class ProfileActivity extends Activity {

	private TextView txtName;
	private TextView txtCI;
	private TextView txtTZ;
	private TextView txtLang;
	private TextView txtEmail;
	private TextView txtCompany;
	private ImageView imgPhoto;
	private Configuration config;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		// Activar el Boton Home
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Lineas para habilitar el acceso a la red y poder conectarse al
		// servidor de OpenERP en el Hilo Principal
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Crear una instancia de la Clase de Configuraciones
		config = new Configuration(this);

		txtName = (TextView) findViewById(R.id.txtName);
		txtCI = (TextView) findViewById(R.id.txtCI);
		txtTZ = (TextView) findViewById(R.id.txtTZ);
		txtLang = (TextView) findViewById(R.id.txtLang);
		txtEmail = (TextView) findViewById(R.id.txtEmail);
		txtCompany = (TextView) findViewById(R.id.txtCompany);
		imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
	}

	void show_employee_info() {
		// Cargar el Nombre de Usuario
		txtCI.setText(config.getCI());
		txtName.setText(config.getName());
		txtTZ.setText(config.getTz());
		txtLang.setText(config.getLang());
		txtEmail.setText(config.getEmail());
		txtCompany.setText(config.getCompany());

		if (config.getPhoto() != null) {
			// Cargar la Foto
			byte[] photo = Base64.decode(config.getPhoto(), Base64.DEFAULT);
			Bitmap bmp = BitmapFactory.decodeByteArray(photo, 0, photo.length);
			imgPhoto.setImageBitmap(bmp);
		}
	}

	@Override
	protected void onStart() {
		show_employee_info();
		super.onStart();
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
					OpenErpConnect oerp = OpenErpConnect.connect(Server, port, database, user, pass);
					if (oerp != null) {
						ConfigActivity ConfigActivity_obj = new ConfigActivity();
						if (ConfigActivity_obj.save_employee_info(config, config.getEmployeeID(), oerp)) {
							Toast.makeText(this, "Datos Actualizados correctamente.", Toast.LENGTH_SHORT).show();
							show_employee_info();
						}
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
