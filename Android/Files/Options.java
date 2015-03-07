package com.log.wiicar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Options extends Activity {
    Button setip, gps, sound, back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options);
		setip = (Button) findViewById(R.id.setip);
		gps = (Button) findViewById(R.id.gps);
		sound = (Button) findViewById(R.id.sound);
		back = (Button) findViewById(R.id.back);
		setip.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent startip = new Intent("com.WiiCar.CONNECTIP");
				startActivity(startip);
				
			}
		});
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent startip = new Intent("com.WiiCar.HOME");
				startActivity(startip);
			}
		});
		
		
	}

	
}
