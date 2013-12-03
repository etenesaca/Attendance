package com.openerp.attendances.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.openerp.attendances.R;

public class RegisterActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_);
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
		case R.id.mnExit:
			// Para cerrara la aplicacion
			finish();
			break;
		case R.id.mnSettings:
			// Para ir a la ventana se Configuraciones
			Intent ventana_menu = new Intent(RegisterActivity.this, ConfigActivity.class);
			startActivity(ventana_menu);
			break;

		default:
			break;
		}
		return true;
	}
}
