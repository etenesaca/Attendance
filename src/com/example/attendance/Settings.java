package com.example.attendance;

//--Imports Agregados para que no vuelva dar error al hacer conexiones XMLRPC
import android.os.Build;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
//----------------------------------------------------------------------------

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import android.os.Bundle;
import android.os.StrictMode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.AndroidCharacter;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
		final EditText txtxUsername = (EditText) findViewById(R.id.txtxUsername);
		final EditText txtPassword = (EditText) findViewById(R.id.txtPassword);

		// ---Combobox de las Base de Datos
		final Spinner cmbDb = (Spinner) findViewById(R.id.cmbDb);

		cmbDb.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				String server = txtHost.getText().toString();
				String port_str = txtPort.getText().toString();
				if (server == "" | port_str == "") {
					return false;
				}
				int port = Integer.parseInt(txtPort.getText().toString());

				boolean TestConnection = OpenErpConnect.TestConnection(server, port);
				ArrayAdapter<String> adaptador;
				if (TestConnection) {
					String[] list_db = OpenErpConnect.getDatabaseList(server, port);
					adaptador = new ArrayAdapter<String>(Settings.this, android.R.layout.simple_spinner_item, list_db);
				} else {
					String[] list_db = {};
					adaptador = new ArrayAdapter<String>(Settings.this, android.R.layout.simple_spinner_item, list_db);
					// TODO - Enviar un mensaje diciendoque no se puede conectar
					// al Servidor
					AlertDialog.Builder dlgAlert = new AlertDialog.Builder(Settings.this);
					dlgAlert.setTitle("Error de Conexión").setIcon(android.R.drawable.ic_delete);;
					dlgAlert.setMessage("No se pudo conectar al servidor, Verifque los parametros de Conexión.");
					dlgAlert.setPositiveButton("OK", null);
					dlgAlert.setCancelable(true);
					dlgAlert.create().show();
				}
				cmbDb.setAdapter(adaptador);
				return false;
			}
		});

		// ---Boton Login
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
				final String user = txtxUsername.getText().toString();
				final String pass = txtPassword.getText().toString();

				if ((server == "") || (port_str == "") || (db == "") || (user == "") || (pass == "")) {
					AlertDialog.Builder dlgAlert = new AlertDialog.Builder(Settings.this);
					dlgAlert.setTitle("Advertencia").setIcon(android.R.drawable.ic_delete);
					dlgAlert.setMessage("Antes de Guarda primero llene todos los campos.");
					dlgAlert.setPositiveButton("OK", null);
					dlgAlert.setCancelable(true);
					dlgAlert.create().show();
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
					builder.setTitle("Guardar").setMessage("¿Guardar los datos Ahora?").setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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
								dlgAlert.setTitle("Info").setIcon(android.R.drawable.ic_menu_save);
								dlgAlert.setMessage("Lo Datos Se Guardaron Correctamente.");
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
