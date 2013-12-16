package com.openerp.attendances.activities;

import java.net.MalformedURLException;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.openerp.attendances.Configuration;
import com.openerp.attendances.OpenErpConnect;
import com.openerp.attendances.R;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class RegisterActivity extends Activity implements OnClickListener {
	private Configuration config;
	private QuickContactBadge pctFoto;
	private Button btnRefresh;
	private Button btnRegisterAttendance;
	private TextView txtNombre;
	private TextView txtLastRegister;
	private String RegisterType;

	@Override
	protected void onStart() {
		refresh_connection();
		refresh_user();
		super.onStart();
	}

	private LinearLayout contenedor_error_connection;
	private LinearLayout contenedor_register;
	private LinearLayout contenedor_without_account;
	private LinearLayout contenedor_menu;
	private ImageView imgAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		// Lineas para habilitar el acceso a la red y poder conectarse al
		// servidor de OpenERP en el Hilo Principal
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Crear una instancia de la Clase de Configuraciones
		config = new Configuration(this);

		pctFoto = (QuickContactBadge) findViewById(R.id.pctFoto);
		pctFoto.setOnClickListener(this);

		btnRefresh = (Button) findViewById(R.id.btnRefresh);
		btnRefresh.setOnClickListener(this);

		btnRegisterAttendance = (Button) findViewById(R.id.btnRegisterAttendance);
		btnRegisterAttendance.setOnClickListener(this);

		txtNombre = (TextView) findViewById(R.id.txtName);
		txtLastRegister = (TextView) findViewById(R.id.txtLastRegister);
		imgAction = (ImageView) findViewById(R.id.imgAction);

		contenedor_menu = (LinearLayout) findViewById(R.id.contenedor_menu);
		contenedor_error_connection = (LinearLayout) findViewById(R.id.contenedor_error_connection);
		contenedor_register = (LinearLayout) findViewById(R.id.contenedor_register);
		contenedor_without_account = (LinearLayout) findViewById(R.id.contenedor_without_account);
	}

	void refresh_user() {
		// Cargar el Nombre de Usuario
		txtNombre.setText(config.getName());

		if (config.getPhoto() != null) {
			// Cargar la Foto
			byte[] photo = Base64.decode(config.getPhoto(), Base64.DEFAULT);
			Bitmap bmp = BitmapFactory.decodeByteArray(photo, 0, photo.length);
			pctFoto.setImageBitmap(bmp);
		}
	}

	void refresh_connection() {
		// Verificar si hay conexion
		String Server = config.getServer();
		String database = config.getDataBase();
		String user = config.getLogin();
		String pass = config.getPassword();
		if (Server != null && config.getPort() != null && config.getDataBase() != null && config.getUserID() != null) {
			if (OpenErpConnect.TestConnection(config.getServer(), Integer.parseInt(config.getPort()))) {
				imgAction.setImageDrawable(getResources().getDrawable(R.drawable.stop));
				btnRegisterAttendance.setEnabled(false);
				btnRegisterAttendance.setText("Registrar Asistencia");

				contenedor_menu.setVisibility(View.VISIBLE);
				contenedor_error_connection.setVisibility(View.INVISIBLE);
				contenedor_without_account.setVisibility(View.INVISIBLE);
				contenedor_register.setVisibility(View.VISIBLE);

				// Verificar si debe registrar una entrada o una salida
				Integer port = Integer.parseInt(config.getPort());
				Integer uid = Integer.parseInt(config.getUserID());
				Integer employeeID = Integer.parseInt(config.getEmployeeID());
				try {
					OpenErpConnect conn = new OpenErpConnect(Server, port, database, user, pass, uid);
					if (conn != null) {
						String ValidateRegister = conn.ValidateRegister();

						AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
						dlgAlert.setTitle("Advertencia").setIcon(android.R.drawable.ic_delete);
						dlgAlert.setPositiveButton("OK", null);
						dlgAlert.setCancelable(true);
						if (ValidateRegister.equals("no_empleado")) {
							dlgAlert.setMessage("Usted ya no esta registrado como Empleado");
							dlgAlert.create().show();
							return;
						}
						if (ValidateRegister.equals("no_config")) {
							dlgAlert.setMessage("Eñ sistema no tiene configurados los dias laborables.");
							dlgAlert.create().show();
							return;
						}
						if (ValidateRegister.equals("no_day")) {
							Toast msg = Toast.makeText(this, "Hoy no es un día laborable", Toast.LENGTH_LONG);
							msg.show();
						} else {
							HashMap<String, Object> last_register = conn.getLastRegisterToday(employeeID);
							if (Boolean.parseBoolean(last_register.get("has_register_today") + "")) {
								txtLastRegister.setVisibility(View.VISIBLE);
								if (last_register.get("type").equals("in")) {
									txtLastRegister.setText("Entrada: " + last_register.get("time"));
									txtLastRegister.setTextColor(getResources().getColor(R.color.green));
								} else {
									txtLastRegister.setText("Salida: " + last_register.get("time"));
									txtLastRegister.setTextColor(getResources().getColor(R.color.red));
								}
							} else {
								txtLastRegister.setVisibility(View.INVISIBLE);
							}

							String[] parts = ValidateRegister.split("_");
							if (parts[0].equals("in")) {
								RegisterType = "in";
								btnRegisterAttendance.setText("Registrar Entrada");
								btnRegisterAttendance.setEnabled(true);
								imgAction.setImageDrawable(getResources().getDrawable(R.drawable.up));
							} else if (parts[0].equals("out")) {
								RegisterType = "out";
								btnRegisterAttendance.setText("Registrar Salida");
								btnRegisterAttendance.setEnabled(true);
								imgAction.setImageDrawable(getResources().getDrawable(R.drawable.down));
							} else if (parts[0].equals("notime")) {
								Toast msg = Toast.makeText(this, "Hoy " + parts[1] + " no se puede registrar asistencia después de las " + parts[2], Toast.LENGTH_LONG);
								msg.show();
							}
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}

			} else {
				contenedor_menu.setVisibility(View.VISIBLE);
				contenedor_register.setVisibility(View.INVISIBLE);
				contenedor_without_account.setVisibility(View.INVISIBLE);
				contenedor_error_connection.setVisibility(View.VISIBLE);
			}
		} else {
			contenedor_menu.setVisibility(View.INVISIBLE);
			contenedor_error_connection.setVisibility(View.INVISIBLE);
			contenedor_register.setVisibility(View.INVISIBLE);
			contenedor_without_account.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnRegisterAttendance:
			// Registrar la asistencia
			if (OpenErpConnect.TestConnection(config.getServer(), Integer.parseInt(config.getPort()))) {
				String Server = config.getServer();
				String database = config.getDataBase();
				String user = config.getLogin();
				String pass = config.getPassword();
				Integer port = Integer.parseInt(config.getPort());
				Integer uid = Integer.parseInt(config.getUserID());
				Integer employeeID = Integer.parseInt(config.getEmployeeID());

				OpenErpConnect conn;
				try {
					conn = new OpenErpConnect(Server, port, database, user, pass, uid);
					if (conn != null) {
						AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
						dlgAlert.setPositiveButton("OK", null);
						dlgAlert.setCancelable(true);
						dlgAlert.setTitle("Error").setIcon(android.R.drawable.ic_delete);

						HashMap<String, Object> last_register = conn.getLastRegisterToday(employeeID);
						if (Boolean.parseBoolean(last_register.get("has_register_today") + "")) {
							if (RegisterType.equals("in") && last_register.get("type").equals("in")) {
								dlgAlert.setMessage("¡Ya se registro la Entrada a las: " + last_register.get("time") + " !");
								dlgAlert.create().show();
								refresh_connection();
								return;
							} else if (RegisterType.equals("out") && last_register.get("type").equals("out")) {
								dlgAlert.setMessage("¡Ya se registro la Salida a las: " + last_register.get("time") + " !");
								dlgAlert.create().show();
								refresh_connection();
								return;
							}
						}
						boolean result = conn.Register_Attendance(employeeID);
						if (result) {
							Toast msg = Toast.makeText(this, "Registro Guardado Correctamente", Toast.LENGTH_SHORT);
							msg.show();
						} else {
							dlgAlert.setMessage("Upps.. Algo a salido mal y no se pudo guardar el registro");
							dlgAlert.create().show();
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			refresh_connection();
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
		case R.id.mnRefresh:
			refresh_connection();
			refresh_user();
			break;

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

		case R.id.mnSettings2:
			// Para ir a la ventana se Configuraciones
			Intent config_act2 = new Intent(RegisterActivity.this, ConfigActivity.class);
			startActivity(config_act2);
			break;

		case R.id.mnprofile:
			// Ir al perfil del Usuario
			Intent profile_act = new Intent(this, ProfileActivity.class);
			startActivity(profile_act);
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

	@Override
	public boolean onSearchRequested() {
		// Para ir a la ventana se Configuraciones
		Intent search_act = new Intent(RegisterActivity.this, SearchActivity.class);
		startActivity(search_act);
		return super.onSearchRequested();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {

		}
		return super.onKeyDown(keyCode, event);
	}
}
