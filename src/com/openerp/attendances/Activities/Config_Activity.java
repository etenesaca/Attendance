package com.openerp.attendances.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Spinner;

import com.openerp.attendances.R;

public class Config_Activity extends Activity implements OnClickListener, OnTouchListener {
	// Declare Elements
	private Button btnCancel;
	private Button btnSave;
	private Spinner cmbDb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config);

		// Boton Guardar
		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(this);

		// Boton Cancelar
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);

		// LIsta de Bases de Datos
		cmbDb = (Spinner) findViewById(R.id.cmbDb);
		cmbDb.setOnTouchListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSave:
			finish();
			break;
		case R.id.btnCancel:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.cmbDb:
			//TODO
			break;

		default:
			break;
		}
		return false;
	}
}
