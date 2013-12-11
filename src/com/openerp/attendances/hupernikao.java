package com.openerp.attendances;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class hupernikao {

	public static int ConvertStringtoDateSeconds(String date_str) {
		return ConvertStringtoDateSeconds(date_str, "yyyy-MM-dd HH:mm:ss");
	}

	public static int ConvertStringtoDateSeconds(String date_str, String formato) {
		int result = 0;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatoFecha = new SimpleDateFormat(formato);
		try {
			Date date_dt = formatoFecha.parse(date_str);
			cal.setTime(date_dt);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
			int seconds = cal.get(Calendar.SECOND);

			result = (hour * 3600) + (minute * 60) + seconds;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static HashMap<String, Object> ConvertDatetoString(HashMap<String, Object> range_dates) {
		return ConvertDatetoString(range_dates, "yyyy-MM-dd");
	}

	public static HashMap<String, Object> ConvertDatetoString(HashMap<String, Object> range_dates, String formato) {
		SimpleDateFormat formatoFecha = new SimpleDateFormat(formato);
		int year, month, day;
		Calendar cal = Calendar.getInstance();

		// DATE START
		String date_start = range_dates.get("date_start") + "";
		Date date_start_dt;
		try {
			date_start_dt = formatoFecha.parse(date_start);
			cal.setTime(date_start_dt);
			month = cal.get(Calendar.MONTH) + 1;
			day = cal.get(Calendar.DAY_OF_MONTH);
			year = cal.get(Calendar.YEAR);
			date_start = day + "/" + month + "/" + year;
			range_dates.put("date_start", date_start);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// DATE STOP
		String date_stop = range_dates.get("date_stop") + "";
		Date date_stop_dt;
		try {
			date_stop_dt = formatoFecha.parse(date_stop);
			cal.setTime(date_stop_dt);
			month = cal.get(Calendar.MONTH) + 1;
			day = cal.get(Calendar.DAY_OF_MONTH);
			year = cal.get(Calendar.YEAR);
			date_stop = day + "/" + month + "/" + year;
			range_dates.put("date_stop", date_stop);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return range_dates;
	}

	public static String ConvertToHourFormat(float hour) {
		return ConvertToHourFormat(hour, false);
	}

	public static String ConvertToHourFormat(float hour, boolean with_seconds) {
		String result = "";
		int hours = (int) (hour / 3600);
		float res_hours = hour % 3600;

		int mins = (int) (res_hours / 60);
		float res_mins = res_hours % 60;

		int secs = (int) res_mins;

		result = CompletarCadena(hours + "") + ":" + CompletarCadena(mins + "");
		if (with_seconds) {
			result = result + ":" + CompletarCadena(secs + "");
		}
		return result;
	}

	public static String CompletarCadena(String cadena) {
		return CompletarCadena(cadena, 2);
	}

	public static String CompletarCadena(String cadena, int num) {
		return CompletarCadena(cadena, num, false);
	}

	public static String CompletarCadena(String cadena, int num, boolean right) {
		return CompletarCadena(cadena, num, right, "0");
	}

	public static String CompletarCadena(String cadena, int num, boolean right, String character) {
		while (cadena.length() < num) {
			if (right) {
				cadena = cadena + character;
			} else {
				cadena = character + cadena;
			}
		}
		return cadena;
	}
}
