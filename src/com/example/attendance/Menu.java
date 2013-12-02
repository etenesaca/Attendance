package com.example.attendance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Menu extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		// Boton Ir a Configuraciones
		Button btnSettings = (Button) findViewById(R.id.btnSettings);
		btnSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent ventana_menu = new Intent(Menu.this, Settings.class);
				startActivity(ventana_menu);
			}
		});

		// Boton Ir a Perfil
		Button btnProfile = (Button) findViewById(R.id.btnProfile);
		btnProfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent ventana_menu = new Intent(Menu.this, Profile.class);
				startActivity(ventana_menu);
			}
		});
	}
}
