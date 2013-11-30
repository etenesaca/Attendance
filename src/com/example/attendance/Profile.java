package com.example.attendance;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.QuickContactBadge;
import android.widget.TextView;

public class Profile extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		configuration config = new configuration(Profile.this);
		String name = config.getName();

		// Cargar los datos
		TextView lblNombre = (TextView) findViewById(R.id.lblNombre);
		lblNombre.setText(name);

		QuickContactBadge pctFoto = (QuickContactBadge) findViewById(R.id.pctFoto);
		byte[] photo = Base64.decode(config.getPhoto(), Base64.DEFAULT);
		Bitmap bmp = BitmapFactory.decodeByteArray(photo, 0, photo.length);
		pctFoto.setImageBitmap(bmp);
	}
}
