package com.openerp.attendances.activities;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.openerp.attendances.Configuration;
import com.openerp.attendances.Item;
import com.openerp.attendances.ItemAdapter;
import com.openerp.attendances.OpenErpConnect;
import com.openerp.attendances.R;
import com.openerp.attendances.hupernikao;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class SearchActivity extends Activity {
	private ProgressDialog progressDialog;
	private Configuration config;
	private EditText txtFrom;
	private EditText txtTo;
	private TextView txtTotalHours;
	private TextView txtTotalHours_Now;
	private TextView txtTotalHours_Week;
	private ListView lstAttendances;
	private ListView lstExtraHours;

	Timer T;
	int seconds_check_in;
	float hours_by_week;
	int total_seconds;
	int total_seconds_in_this_week;
	boolean sum_seconds = false;

	TabHost contenedorPestania;
	TabSpec pestania;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Display a indeterminate progress bar on title bar
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.activity_search);

		// Lineas para habilitar el acceso a la red y poder conectarse al
		// servidor de OpenERP en el Hilo Principal
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Activar el Boton Home
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

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

		// Crear una instancia de la Clase de Configuraciones
		config = new Configuration(this);

		// Declaracion de Elementos
		txtFrom = (EditText) findViewById(R.id.txtFrom);
		txtTo = (EditText) findViewById(R.id.txtTo);
		txtTotalHours = (TextView) findViewById(R.id.txtTotalHours);
		txtTotalHours_Now = (TextView) findViewById(R.id.txtTotalHours_Now);
		txtTotalHours_Week = (TextView) findViewById(R.id.txtTotalHours_Week);

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

		T = new Timer();
		T.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (sum_seconds) {
							Calendar fecha = new GregorianCalendar();
							int hour = fecha.get(Calendar.HOUR_OF_DAY);
							int minute = fecha.get(Calendar.MINUTE);
							int seconds = fecha.get(Calendar.SECOND);
							int seconds_now = (hour * 3600) + (minute * 60) + seconds;
							int transcurrido = seconds_now - seconds_check_in;
							String with_now = hupernikao.ConvertToHourFormat(transcurrido, true);
							txtTotalHours_Now.setText("00:00:00 " + with_now);

							// Calcular las horas restantes en esta semana
							int seconds_by_week = (int) (hours_by_week * 3600);
							int faltantes = seconds_by_week - (transcurrido + total_seconds_in_this_week);
							String faltantes_str = "00.00:00";
							if (faltantes > 0) {
								faltantes_str = hupernikao.ConvertToHourFormat(faltantes, true);
							}
							txtTotalHours_Week.setText("" + faltantes_str);

							// Sumar el total de horas mas ahora
							int total_hours_with_now = (int) (total_seconds + transcurrido);
							String total_hours_with_now_str = hupernikao.ConvertToHourFormat(total_hours_with_now, true);
							txtTotalHours_Now.setText(total_hours_with_now_str + " " + with_now);
						}
					}
				});
			}
		}, 1000, 1000);
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

						// Procesar los registros de asistencia
						Object[] attendances = (Object[]) registers_dict.get("attendance_registers");
						List<Item> items_attendances = new ArrayList<Item>();

						String last_attendance = "";
						String last_date = "";
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
							items_attendances.add(new Item(icon, description));
							last_attendance = type;
							last_date = date + " " + time;
						}
						// Poner la lista de elementos al ListView
						lstAttendances.setAdapter(new ItemAdapter(this, items_attendances));

						// Obtener las horas laborables por semana
						String hours_by_week_str = registers_dict.get("hours_by_week") + "";
						hours_by_week = Float.parseFloat(hours_by_week_str);

						// Obtener el tiempo total en el rango de fechas
						String total_seconds_str = registers_dict.get("total_seconds") + "";
						total_seconds = Integer.parseInt(total_seconds_str);

						// Obtener el tiempo total laborado por semana en segund
						String total_seconds_in_this_week_str = registers_dict.get("total_seconds_in_this_week") + "";
						total_seconds_in_this_week = Integer.parseInt(total_seconds_in_this_week_str);

						// Sacar el total de Horas
						String total_hours_format_hour = registers_dict.get("total_hours_format_hour") + "";
						txtTotalHours.setText("Completadas: " + total_hours_format_hour);

						if (last_attendance.equals("Entrada")) {
							seconds_check_in = hupernikao.ConvertStringtoDateSeconds(last_date);
							txtTotalHours_Now.setVisibility(View.VISIBLE);
							sum_seconds = true;
						} else {
							txtTotalHours_Now.setVisibility(View.INVISIBLE);
							txtTotalHours_Now.setText("");

							// Poner la horas restantes por laborar esta semana
							int seconds_by_week = (int) (hours_by_week * 3600);
							int faltantes = seconds_by_week - total_seconds_in_this_week;
							String faltantes_str = "00.00:00";
							if (faltantes > 0) {
								faltantes_str = hupernikao.ConvertToHourFormat(faltantes);
							}
							txtTotalHours_Week.setText("" + faltantes_str);

							sum_seconds = false;
						}

						// Procesar Horas Extras
						Object[] extra_hours = (Object[]) registers_dict.get("extra_hours");
						List<Item> items_extra_hours = new ArrayList<Item>();

						for (int i = 0; i < extra_hours.length; i++) {
							HashMap<String, Object> item = (HashMap<String, Object>) extra_hours[i];
							String date = (String) item.get("date");
							String hours = (String) item.get("hours");
							String motive = (String) item.get("description");
							String type = (String) item.get("type");

							int icon = R.drawable.remove;
							if (type.equals("add")) {
								icon = R.drawable.add;
							}

							String description = date + " [" + hours + "]" + " " + motive;
							items_extra_hours.add(new Item(icon, description));
						}
						// Poner la lista de elementos al ListView
						lstExtraHours.setAdapter(new ItemAdapter(this, items_extra_hours));
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

		if (item.getItemId() == R.id.mnSearch_Today || item.getItemId() == R.id.mnSearch_Today2) {
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
		} else if (item.getItemId() == R.id.mnSearch_Yesterday || item.getItemId() == R.id.mnSearch_Yesterday2) {
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
		} else if (item.getItemId() == R.id.mnSearch_This_Week || item.getItemId() == R.id.mnSearch_This_Week2) {
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
		} else if (item.getItemId() == R.id.mnSearch_This_Month || item.getItemId() == R.id.mnSearch_This_Month2) {
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
		} else if (item.getItemId() == android.R.id.home) {
			// Reggresar al activity de registro de asistencias
			finish();
		}

		return true;
	}
}
