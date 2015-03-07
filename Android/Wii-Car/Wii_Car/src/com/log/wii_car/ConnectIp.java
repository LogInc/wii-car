package com.log.wii_car;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ConnectIp extends Activity {

	Button connect, back;
	TextView status;
	String ip;
	public String ipADD;
	EditText ipaddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.connectip);
		connect = (Button) findViewById(R.id.connect);
		ipaddress = (EditText) findViewById(R.id.address);
		back = (Button) findViewById(R.id.back);
		status = (TextView) findViewById(R.id.status);

		connect.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				ip = ipaddress.getText().toString();
				status.setText("Connecting...");

				if (ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
					final Handler handler = new Handler();
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							// do something after 2.5s
							status.setText("Connected to" + ip);
							connect.setBackgroundResource(R.drawable.connected);
						}

					}, 2500);
				} else {
					status.setText("Incorrect Ip");
				}

				/*
				 * if (!ip.isEmpty()) { // sleeping.start();
				 * 
				 * status.setText("Connecting..."); try {
				 * 
				 * Thread.currentThread(); Thread.sleep(3000); } catch
				 * (InterruptedException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); }
				 * 
				 * connect.setBackgroundResource(R.drawable.connected);
				 * 
				 * } else{ status.setText("Incorrect ip format"); }
				 */

			}
		});
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent startip = new Intent("com.log.wii_car.OPTIONS");
				startActivity(startip);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
		
		
	}

}
