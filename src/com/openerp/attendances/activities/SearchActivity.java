package com.openerp.attendances.activities;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.openerp.attendances.Configuration;
import com.openerp.attendances.OpenErpConnect;
import com.openerp.attendances.R;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class SearchActivity extends Activity {
	private ProgressDialog progressDialog;
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
		// Display a indeterminate progress bar on title bar
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

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
		// Sets the visibility of the indeterminate progress bar in the
		// title
		setProgressBarIndeterminateVisibility(true);
		// Show progress dialog
		progressDialog = ProgressDialog.show(this, "ProgressDialog", "Loading!");

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

						List<Item> items = new ArrayList<Item>();

						attandence_list = new String[attendances.length];
						for (int i = 0; i < attendances.length; i++) {
							HashMap<String, Object> item = (HashMap<String, Object>) attendances[i];
							String date = (String) item.get("date");
							String time = (String) item.get("time");
							String duration = (String) item.get("duration");
							String type = (String) item.get("type");
							String description = date + " [" + time + "]" + " " + duration + " " + type;

							int icon = R.drawable.up;
							if (type.equals("Salida")) {
								icon = R.drawable.down;
							}
							items.add(new Item(icon, description));
						}

						// Sets the data behind this ListView
						lstAttendances.setAdapter(new ItemAdapter(this, items));

						// adaptador = new ArrayAdapter<String>(this,
						// android.R.layout.simple_spinner_item,
						// attandence_list);
						// lstAttendances.setAdapter(adaptador);

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
		setProgressBarIndeterminateVisibility(false);
		progressDialog.dismiss();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String Server = config.getServer();
		String database = config.getDataBase();
		String user = config.getLogin();
		String pass = config.getPassword();

		switch (item.getItemId()) {
		case R.id.mnSearch_Today:
			if (OpenErpConnect.TestConnection(config.getServer(), Integer.parseInt(config.getPort()))) {
				Integer port = Integer.parseInt(config.getPort());
				Integer uid = Integer.parseInt(config.getUserID());
				OpenErpConnect conn;
				try {
					conn = new OpenErpConnect(Server, port, database, user, pass, uid);
					if (conn != null) {
						HashMap<String, Object> range_dates = conn.getRangeDates_today();
						txtFrom.setText(range_dates.get("date_start") + "");
						txtTo.setText(range_dates.get("date_stop") + "");
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				BuildSearchRegister();
			}
			break;

		case R.id.mnSearch_Yesterday:
			if (OpenErpConnect.TestConnection(config.getServer(), Integer.parseInt(config.getPort()))) {
				Integer port = Integer.parseInt(config.getPort());
				Integer uid = Integer.parseInt(config.getUserID());
				OpenErpConnect conn;
				try {
					conn = new OpenErpConnect(Server, port, database, user, pass, uid);
					if (conn != null) {
						HashMap<String, Object> range_dates = conn.getRangeDates_yesterday();
						txtFrom.setText(range_dates.get("date_start") + "");
						txtTo.setText(range_dates.get("date_stop") + "");
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				BuildSearchRegister();
			}
			break;

		case R.id.mnSearch_This_Week:
			if (OpenErpConnect.TestConnection(config.getServer(), Integer.parseInt(config.getPort()))) {
				Integer port = Integer.parseInt(config.getPort());
				Integer uid = Integer.parseInt(config.getUserID());
				OpenErpConnect conn;
				try {
					conn = new OpenErpConnect(Server, port, database, user, pass, uid);
					if (conn != null) {
						HashMap<String, Object> range_dates = conn.getRangeDates_this_week();
						txtFrom.setText(range_dates.get("date_start") + "");
						txtTo.setText(range_dates.get("date_stop") + "");
					}
					BuildSearchRegister();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			break;

		case R.id.mnSearch_This_Month:
			if (OpenErpConnect.TestConnection(config.getServer(), Integer.parseInt(config.getPort()))) {
				Integer port = Integer.parseInt(config.getPort());
				Integer uid = Integer.parseInt(config.getUserID());
				OpenErpConnect conn;
				try {
					conn = new OpenErpConnect(Server, port, database, user, pass, uid);
					if (conn != null) {
						HashMap<String, Object> range_dates = conn.getRangeDates_this_month();
						txtFrom.setText(range_dates.get("date_start") + "");
						txtTo.setText(range_dates.get("date_stop") + "");
					}
					BuildSearchRegister();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			break;

		case R.id.mnSearch_Exit:
			// Para cerrara las ventana se busquedas
			finish();
			break;

		default:
			break;
		}
		return true;
	}
}
