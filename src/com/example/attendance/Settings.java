package com.example.attendance;

import java.net.MalformedURLException;
import java.net.URL;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class Settings extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); 
		StrictMode.setThreadPolicy(policy);
		final TextView txtDisplay = (TextView) findViewById(R.id.lblServer);

		// ---Combobox
		Spinner cmbDb = (Spinner) findViewById(R.id.cmbDb);
		cmbDb.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				
			}
		});
		
		// ---Boton Login
		Button btnLogin = (Button) findViewById(R.id.btnTest);
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String server = "192.168.10.103";
				int port = 8069;
				String db = "kemas7";
				String user = "admin";
				String pass = "admin";

				OpenErpConnect connection = OpenErpConnect.connect(server,port, db, user, pass);
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
