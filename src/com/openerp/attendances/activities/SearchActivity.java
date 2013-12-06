package com.openerp.attendances.activities;

import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.openerp.attendances.Configuration;
import com.openerp.attendances.OpenErpConnect;
import com.openerp.attendances.R;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class SearchActivity extends Activity {
	private Configuration config;
	private EditText txtFrom;
	private EditText txtTo;
	private ListView lstRegisters;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		// Lineas para habilitar el acceso a la red y poder conectarse al
		// servidor de OpenERP en el Hilo Principal
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Crear una instancia de la Clase de Configuraciones
		config = new Configuration(this);

		// Declaracion de Elementos
		txtFrom = (EditText) findViewById(R.id.txtFrom);
		txtTo = (EditText) findViewById(R.id.txtTo);
		lstRegisters = (ListView) findViewById(R.id.lstRegisters);

		// Poner por defecto la fecha del dia de Hoy
		Calendar fecha = new GregorianCalendar();
		int anio = fecha.get(Calendar.YEAR);
		int mes = fecha.get(Calendar.MONTH);
		int dia = fecha.get(Calendar.DAY_OF_MONTH);
		String Hoy = dia + "/" + (mes + 1) + "/" + anio;

		txtFrom.setText(Hoy);
		txtTo.setText(Hoy);
		// Consultar datos
		BuildSearchRegister();
	}

	public void SearchOnClick(View view) {
		Toast msg = Toast.makeText(this, "Buscando..", Toast.LENGTH_SHORT);
		msg.show();
		BuildSearchRegister();
	}

	void BuildSearchRegister() {
		// Mandar a buscar registros con un rango de fechas
		String Server = config.getServer();
		String database = config.getDataBase();
		String user = config.getLogin();
		String pass = config.getPassword();

		if (Server != null && config.getPort() != null && config.getDataBase() != null && config.getUserID() != null) {
			// Verificar si hay conexion
			if (OpenErpConnect.TestConnection(config.getServer(), Integer.parseInt(config.getPort()))) {
				Integer port = Integer.parseInt(config.getPort());
				Integer uid = Integer.parseInt(config.getUserID());
				try {
					OpenErpConnect conn = new OpenErpConnect(Server, port, database, user, pass, uid);
					if (conn != null) {
						ArrayAdapter<String> adaptador;
						String[] register_list = conn.getRegisters(txtFrom.getText().toString(), txtTo.getText().toString(), Integer.parseInt(config.getEmployeeID()));
						adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, register_list);
						lstRegisters.setAdapter(adaptador);
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
