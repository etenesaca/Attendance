package com.openerp.attendances.activities;

import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

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
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
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
	private TextView txtTotalHours;
	private ListView lstAttendances;
	private ListView lstExtraHours;

	TabHost contenedorPestania;
	TabSpec pestania;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		// Inicializar las Pestañas
		contenedorPestania = (TabHost) findViewById(android.R.id.tabhost);
		contenedorPestania.setup();

		pestania = contenedorPestania.newTabSpec("pestana1");
		pestania.setContent(R.id.tabAttendances);
		pestania.setIndicator("Asistencias", getResources().getDrawable(android.R.drawable.ic_menu_help));
		contenedorPestania.addTab(pestania);

		pestania = contenedorPestania.newTabSpec("pestana1");
		pestania.setContent(R.id.tabExtraHours);
		pestania.setIndicator("Reajustes", getResources().getDrawable(android.R.drawable.ic_menu_help));
		contenedorPestania.addTab(pestania);

		contenedorPestania.setCurrentTab(0);

		// Lineas para habilitar el acceso a la red y poder conectarse al
		// servidor de OpenERP en el Hilo Principal
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Crear una instancia de la Clase de Configuraciones
		config = new Configuration(this);

		// Declaracion de Elementos
		txtFrom = (EditText) findViewById(R.id.txtFrom);
		txtTo = (EditText) findViewById(R.id.txtTo);
		txtTotalHours = (TextView) findViewById(R.id.txtTotalHours);
		lstAttendances = (ListView) findViewById(R.id.lstAttendances);
		lstExtraHours = (ListView) findViewById(R.id.lstExtraHours);

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
		BuildSearchRegister();
	}

	@SuppressWarnings("unchecked")
	void BuildSearchRegister() {
		Toast msg = Toast.makeText(this, "Buscando..", Toast.LENGTH_SHORT);
		msg.show();
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
						HashMap<String, Object> registers_dict = conn.getRegisters(txtFrom.getText().toString(), txtTo.getText().toString(), Integer.parseInt(config.getEmployeeID()));
						ArrayAdapter<String> adaptador;

						// Procesar los registros de asistencia
						Object[] attendances = (Object[]) registers_dict.get("attendance_registers");
						String[] attandence_list = null;

						attandence_list = new String[attendances.length];
						for (int i = 0; i < attendances.length; i++) {
							HashMap<String, Object> item = (HashMap<String, Object>) attendances[i];
							String date = (String) item.get("date");
							String time = (String) item.get("time");
							String duration = (String) item.get("duration");
							String type = (String) item.get("type");
							attandence_list[i] = date + " [" + time + "]" + " " + duration + " " + type;
						}

						adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, attandence_list);
						lstAttendances.setAdapter(adaptador);

						// Procesar Horas Extras
						Object[] extra_hours = (Object[]) registers_dict.get("extra_hours");
						String[] extra_hours_list = null;

						extra_hours_list = new String[extra_hours.length];
						for (int i = 0; i < extra_hours.length; i++) {
							HashMap<String, Object> item = (HashMap<String, Object>) extra_hours[i];
							String date = (String) item.get("date");
							String hours = (String) item.get("hours");
							String description = (String) item.get("description");
							String type = (String) item.get("type");
							if (type.equals("add")) {
								type = "+";
							} else {
								type = "-";
							}
							extra_hours_list[i] = date + " [" + type + hours + "]" + " " + description;
						}

						// Sacar el total de Horas
						String total_hours = registers_dict.get("total_hours") + "";
						txtTotalHours.setText("Total de horas: " + total_hours);

						adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, extra_hours_list);
						lstExtraHours.setAdapter(adaptador);

					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
