package com.openerp.attendances.activities;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.openerp.attendances.Configuration;
import com.openerp.attendances.OpenErpConnect;
import com.openerp.attendances.R;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class ConfigActivity extends Activity implements OnClickListener, OnTouchListener {
	// Declare Elements
	private Button btnCancel;
	private Button btnSave;
	private Spinner cmbDb;
	private EditText txtServer;
	private EditText txtPort;
	private EditText txtUsername;
	private EditText txtPassword;
	private Configuration config;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config);

		// Lineas para habilitar el acceso a la red y poder conectarse al
		// servidor de OpenERP
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Crear una instancia de la Clase de Configuraciones
		config = new Configuration(this);
		// Boton Guardar
		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(this);

		// Boton Cancelar
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);

		// LIsta de Bases de Datos
		cmbDb = (Spinner) findViewById(R.id.cmbDb);
		cmbDb.setOnTouchListener(this);

		txtServer = (EditText) findViewById(R.id.txtServer);
		txtPort = (EditText) findViewById(R.id.txtPort);
		txtUsername = (EditText) findViewById(R.id.txtUsername);
		txtPassword = (EditText) findViewById(R.id.txtPassword);

		// Cargar los datos desde la configuración
		String Key_SERVER = config.getServer();
		String Key_PORT = config.getPort();
		String Key_DATABASE = config.getDataBase();

		txtServer.setText(Key_SERVER);
		txtPort.setText(config.getPort());
		txtUsername.setText(config.getLogin());
		txtPassword.setText(config.getPassword());

		// Verificar si ya hay guardada una configuracion para Cargar la lista
		// de base de dartos
		if (Key_SERVER != null && Key_PORT != null && Key_DATABASE != null) {
			int saved_port = Integer.parseInt(config.getPort());
			boolean TestConnection = OpenErpConnect.TestConnection(Key_SERVER, saved_port);
			ArrayAdapter<String> adaptador;
			if (TestConnection) {
				String[] list_db = OpenErpConnect.getDatabaseList(Key_SERVER, saved_port);
				adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_db);
				cmbDb.setAdapter(adaptador);
				for (int i = 0; i < list_db.length; i++) {
					if (list_db[i].equals(Key_DATABASE)) {
						cmbDb.setSelection(i);
					}
				}
			} else {
				String[] list_db = {};
				adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_db);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSave:
			String port_str = txtPort.getText().toString();
			final String Server = txtServer.getText().toString();
			String db = "";
			try {
				db = cmbDb.getSelectedItem().toString();
			} catch (Exception e) {
			}
			final String user = txtUsername.getText().toString();
			final String pass = txtPassword.getText().toString();

			AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
			dlgAlert.setTitle("Advertencia").setIcon(android.R.drawable.stat_sys_warning);
			dlgAlert.setPositiveButton("OK", null);
			dlgAlert.setCancelable(true);
			if ("".equals(Server)) {
				dlgAlert.setMessage("Primero Ingrese la Dirección del Servidor.");
				dlgAlert.create().show();
			} else if ("".equals(port_str)) {
				dlgAlert.setMessage("Primero Ingrese el Número del Puerto.");
				dlgAlert.create().show();
			} else if ("".equals(port_str)) {
				dlgAlert.setMessage("Primero Ingrese el Número del Puerto.");
				dlgAlert.create().show();
			} else if ("".equals(db)) {
				dlgAlert.setMessage("Primero Seleccione la Base de Datos.");
				dlgAlert.create().show();
			} else if ("".equals(user)) {
				dlgAlert.setMessage("Ingrese el nombre de Usuario.");
				dlgAlert.create().show();
			} else if ("".equals(pass)) {
				dlgAlert.setMessage("Ingrese el Password.");
				dlgAlert.create().show();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Guardar").setMessage("¿Guardar los datos Ahora?").setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton("Si", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ConfigActivity.this);
						int Port = Integer.parseInt(txtPort.getText().toString());
						if (OpenErpConnect.TestConnection(Server, Port)) {
							OpenErpConnect oerp = OpenErpConnect.connect(Server, Port, cmbDb.getSelectedItem().toString(), user, pass);

							if (oerp == null) {
								dlgAlert.setTitle("Error").setIcon(android.R.drawable.ic_delete);
								dlgAlert.setMessage("Usuario o Contraseña No Válidos.");
								dlgAlert.setPositiveButton("OK", null);
								dlgAlert.setCancelable(true);
								dlgAlert.create().show();
							} else {
								// Leer los datos del perfil del Usuario
								// Logueado
								Long[] ids = { (long) oerp.getUserId() };
								String[] fields = { "name", "image", "image_small" };
								List<HashMap<String, Object>> User_Logged = oerp.read("res.users", ids, fields);

								HashMap<String, Object> aux = User_Logged.get(0);
								String name_user = (String) aux.get("name");
								String image_64 = (String) aux.get("image");

								// Guardar los datos
								config.setServer(Server);
								config.setPort(txtPort.getText().toString());
								config.setDataBase(cmbDb.getSelectedItem().toString());
								config.setLogin(user);
								config.setPassword(pass);

								config.setName(name_user);
								config.setPhoto(image_64);

								Toast msg = Toast.makeText(ConfigActivity.this, "Lo Datos Se Guardaron Correctamente.", Toast.LENGTH_SHORT);
								msg.show();
								finish();
							}
						} else {
							dlgAlert.setTitle("Error").setIcon(android.R.drawable.ic_delete);
							dlgAlert.setMessage("No se pudo conectar al servidor, Verifique los parametros de Conexión.");
							dlgAlert.setPositiveButton("OK", null);
							dlgAlert.setCancelable(true);
							dlgAlert.create().show();
						}
					}
				});
				builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
			break;
		case R.id.btnCancel:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.cmbDb:
			String server = txtServer.getText().toString();
			String port_str = txtPort.getText().toString();
			if (server.equals("") || port_str.equals("")) {
				break;
			}

			String value_server = txtPort.getText().toString() + "";
			String value_port = txtPort.getText().toString() + "";
			if (value_server != "" && value_port != "") {
				int port = Integer.parseInt(txtPort.getText().toString());

				boolean TestConnection = OpenErpConnect.TestConnection(server, port);
				ArrayAdapter<String> adaptador;
				if (TestConnection) {
					String value_database = "";
					try {
						value_database = cmbDb.getSelectedItem().toString();
					} catch (Exception e) {
					}
					String[] list_db = OpenErpConnect.getDatabaseList(server, port);
					adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_db);
					cmbDb.setAdapter(adaptador);
					if (value_database != "") {
						for (int i = 0; i < list_db.length; i++) {
							if (list_db[i].equals(value_database)) {
								cmbDb.setSelection(i);
							}
						}
					}
				} else {
					Toast msg = Toast.makeText(this, "No se pudo conectar al servidor, Verifique los parametros de Conexión.", Toast.LENGTH_SHORT);
					msg.show();
					String[] list_db = {};
					adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_db);
					cmbDb.setAdapter(adaptador);
				}
			}
			break;

		default:
			break;
		}
		return false;
	}
}
