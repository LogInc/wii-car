package com.log.wiicar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

public class Home extends Activity {

	Button drive, options, help;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.home);
		drive = (Button) findViewById(R.id.Drive);
		options = (Button) findViewById(R.id.Options);
		help = (Button) findViewById(R.id.Help);
		drive.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent d = new Intent("com.WiiCar.START");
				startActivity(d);

			}
		});
		options.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent d = new Intent("com.WiiCar.OPTIONS");
				startActivity(d);

			}
		});

	}

}
