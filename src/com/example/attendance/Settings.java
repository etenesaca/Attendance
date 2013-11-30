package com.example.attendance;

//--Imports Agregados para que no vuelva dar error al hacer conexiones XMLRPC
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

//----------------------------------------------------------------------------

@SuppressLint("NewApi")
public class Settings extends Activity {

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		final EditText txtHost = (EditText) findViewById(R.id.txtHost);
		final EditText txtPort = (EditText) findViewById(R.id.txtPort);
		final EditText txtUsername = (EditText) findViewById(R.id.txtxUsername);
		final EditText txtPassword = (EditText) findViewById(R.id.txtPassword);
		final Spinner cmbDb = (Spinner) findViewById(R.id.cmbDb);

		// Cargar los datos desde la configuración

		configuration config = new configuration(Settings.this);
		String Key_SERVER = config.getServer();
		String Key_PORT = config.getPort();
		String Key_DATABASE = config.getDataBase();

		txtHost.setText(Key_SERVER);
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
				adaptador = new ArrayAdapter<String>(Settings.this, android.R.layout.simple_spinner_item, list_db);
				cmbDb.setAdapter(adaptador);
				for (int i = 0; i < list_db.length; i++) {
					if (list_db[i].equals(Key_DATABASE)) {
						cmbDb.setSelection(i);
					}
				}
			} else {
				String[] list_db = {};
				adaptador = new ArrayAdapter<String>(Settings.this, android.R.layout.simple_spinner_item, list_db);
			}
		}

		// ---Combobox de las Base de Datos
		cmbDb.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				String server = txtHost.getText().toString();
				String port_str = txtPort.getText().toString();
				if (server == "" | port_str == "") {
					return false;
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
						adaptador = new ArrayAdapter<String>(Settings.this, android.R.layout.simple_spinner_item, list_db);
						cmbDb.setAdapter(adaptador);
						if (value_database != "") {
							for (int i = 0; i < list_db.length; i++) {
								if (list_db[i].equals(value_database)) {
									cmbDb.setSelection(i);
								}
							}
						}
					} else {
						Toast msg = Toast.makeText(Settings.this, "No se pudo conectar al servidor, Verifique los parametros de Conexión.", Toast.LENGTH_SHORT);
						msg.show();
						String[] list_db = {};
						adaptador = new ArrayAdapter<String>(Settings.this, android.R.layout.simple_spinner_item, list_db);
						cmbDb.setAdapter(adaptador);
					}
				}
				return false;
			}
		});

		// ---Boton Guardar
		Button btnSave = (Button) findViewById(R.id.btnTest);
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final String server = txtHost.getText().toString();
				String port_str = txtPort.getText().toString();
				String db = "";
				try {
					db = cmbDb.getSelectedItem().toString();
				} catch (Exception e) {
				}
				final String user = txtUsername.getText().toString();
				final String pass = txtPassword.getText().toString();

				AlertDialog.Builder dlgAlert = new AlertDialog.Builder(Settings.this);
				dlgAlert.setTitle("Advertencia").setIcon(android.R.drawable.ic_delete);
				dlgAlert.setPositiveButton("OK", null);
				dlgAlert.setCancelable(true);
				if ("".equals(server)) {
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
					AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
					builder.setTitle("Guardar").setMessage("¿Guardar los datos Ahora?").setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton("Si", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							int port = Integer.parseInt(txtPort.getText().toString());
							OpenErpConnect oerp = OpenErpConnect.connect(server, port, cmbDb.getSelectedItem().toString(), user, pass);

							AlertDialog.Builder dlgAlert = new AlertDialog.Builder(Settings.this);
							if (oerp == null) {
								dlgAlert.setTitle("Error").setIcon(android.R.drawable.ic_delete);
								dlgAlert.setMessage("Usuario o Contraseña No Válidos.");
								dlgAlert.setPositiveButton("OK", null);
								dlgAlert.setCancelable(true);
								dlgAlert.create().show();
							} else {
								// Guardar los datos
								configuration conf = new configuration(Settings.this);
								conf.setServer(server);
								conf.setPort(txtPort.getText().toString());
								conf.setDataBase(cmbDb.getSelectedItem().toString());
								conf.setLogin(user);
								conf.setPassword(pass);

								Toast msg = Toast.makeText(Settings.this, "Lo Datos Se Guardaron Correctamente.", Toast.LENGTH_SHORT);
								msg.show();

								// Ir a la ventana de Menu
								Intent ventana_menu = new Intent("com.example.attendance.Register");
								startActivity(ventana_menu);
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
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

}
