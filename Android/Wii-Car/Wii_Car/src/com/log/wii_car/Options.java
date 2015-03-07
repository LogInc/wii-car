package com.log.wii_car;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class Options extends Activity {
	
	 Button setip, gps, sound, back;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.options);
			setip = (Button) findViewById(R.id.setip);
			gps = (Button) findViewById(R.id.gps);
			sound = (Button) findViewById(R.id.sound);
			back = (Button) findViewById(R.id.back);
			setip.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent startip = new Intent("com.log.wii_car.CONNECTIP");
					startActivity(startip);
					
				}
			});
			back.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent GoBack = new Intent("com.log.wii_car.HOME");
					startActivity(GoBack);
				}
			});
			
			
		}

		
	}



