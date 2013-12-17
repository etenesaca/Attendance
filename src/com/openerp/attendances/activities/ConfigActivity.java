package com.openerp.attendances.activities;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
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
		// servidor de OpenERP en el Hilo Principal
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Activar el Boton Home
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

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

	public boolean save_employee_info(Configuration config, String employee_id, OpenErpConnect oerp_connection) {
		long ln_employee_id = Long.parseLong(employee_id + "");
		return save_employee_info(config, ln_employee_id, oerp_connection);
	}

	public boolean save_employee_info(Configuration config, Long[] employee_ids, OpenErpConnect oerp_connection) {
		long employee_id = employee_ids[0];
		return save_employee_info(config, employee_id, oerp_connection);
	}

	public boolean save_employee_info(Configuration config, long employee_id, OpenErpConnect oerp_connection) {
		// Leer los datos del empleado
		String[] fields_employee = { "user_id", "personal_id" };
		Long[] employee_ids = { employee_id };
		List<HashMap<String, Object>> employees = oerp_connection.read("control.horario.employee", employee_ids, fields_employee);
		HashMap<String, Object> employee = employees.get(0);

		// Leer los datos del perfil del Usuario
		Object[] user_tuple = (Object[]) employee.get("user_id");
		Long[] user_ids = { Long.parseLong(user_tuple[0] + "") };
		String[] fields = { "image" };
		List<HashMap<String, Object>> Users = oerp_connection.read("res.users", user_ids, fields);
		HashMap<String, Object> User = Users.get(0);

		// Guardar los datos
		config.setServer(oerp_connection.getServer());
		config.setPort(oerp_connection.getPort() + "");
		config.setDataBase(oerp_connection.getDatabase());
		config.setLogin(oerp_connection.getUserName() + "");
		config.setPassword(oerp_connection.getPassword());
		config.setUserID(oerp_connection.getUserId() + "");
		config.setEmployeeID(employee_ids[0] + "");

		config.setName((String) user_tuple[1] + "");
		config.setCI((String) employee.get("personal_id"));
		config.setPhoto((String) User.get("image"));
		return true;
	}

	public boolean save_employee_info(Configuration config, String employee_id, String Server, int Port, String user, String pass) {
		long ln_employee_id = Long.parseLong(employee_id + "");
		return save_employee_info(config, ln_employee_id, Server, Port, user, pass);
	}

	public boolean save_employee_info(Configuration config, Long[] employee_ids, String Server, int Port, String user, String pass) {
		long employee_id = employee_ids[0];
		return save_employee_info(config, employee_id, Server, Port, user, pass);
	}

	public boolean save_employee_info(Configuration config, long employee_id, String Server, int Port, String user, String pass) {
		OpenErpConnect oerp_connection = OpenErpConnect.connect(Server, Port, cmbDb.getSelectedItem().toString(), user, pass);
		return save_employee_info(config, employee_id, oerp_connection);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSave:
			// Guardar Los datos del Empleado
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
						dlgAlert.setTitle("Error").setIcon(android.R.drawable.ic_delete);
						dlgAlert.setPositiveButton("OK", null);
						dlgAlert.setCancelable(true);

						int Port = Integer.parseInt(txtPort.getText().toString());
						if (OpenErpConnect.TestConnection(Server, Port)) {
							OpenErpConnect oerp = OpenErpConnect.connect(Server, Port, cmbDb.getSelectedItem().toString(), user, pass);

							if (oerp == null) {
								dlgAlert.setMessage("Usuario o Contraseña No Válidos.");
								dlgAlert.create().show();
							} else {
								// Verificar que la base de datos tenga
								// instalado el modulo Control de Horario
								if (!oerp.Module_Installed("control_horario")) {
									dlgAlert.setMessage("La base de datos seleccionada no tiene instalado el Modulo de Control del Horario.");
									dlgAlert.create().show();
								} else {
									// Verificar que el Usuario sea un empleado
									Long[] employee_ids = oerp.search("control.horario.employee", new Object[] { new Object[] { "user_id", "=", oerp.getUserId() } }, 1);
									if (employee_ids.length < 1) {
										dlgAlert.setMessage("La credenciales ingresadas no pertenecen a un Empleado.");
										dlgAlert.create().show();
									} else {
										// Guardar los datos del empleado
										if (save_employee_info(config, employee_ids, oerp)) {
											Toast.makeText(ConfigActivity.this, "Lo Datos Se Guardaron Correctamente.", Toast.LENGTH_SHORT).show();
											finish();
										}
									}
								}
							}
						} else {
							dlgAlert.setMessage("No se pudo conectar al servidor, Verifique los parametros de Conexión.");
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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
