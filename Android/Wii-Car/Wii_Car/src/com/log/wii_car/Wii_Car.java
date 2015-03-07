package com.log.wii_car;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Wii_Car extends Activity implements SensorEventListener {

	private SensorManager sensorManager;
	private Sensor senAccel;
	// InetAddress serveraddress;
	String ipAddress = null;
	String bState = null;
	Button Gas, Brakes, IP;
	ImageView Gear, Dipper, Maps;
	int socketPort = 8888;
	int FR_State = 0;
	int dipper_state = 0;
	int song_state = 0;
	EditText ipaddress;
	//String State;
	TextView display, nowPlaying, gearState;
	byte[] State = { 'S', 'S', '0', '0', '\n' };
	//ConnectIp instance = new ConnectIp();
	Bitmap bMap;
	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_wii__car);

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		senAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, senAccel,
				SensorManager.SENSOR_DELAY_NORMAL);
		
		ipaddress = (EditText) findViewById(R.id.address);
		Gas = (Button) findViewById(R.id.bGas);
		Brakes = (Button) findViewById(R.id.bBrakes);
		Gear = (ImageView) findViewById(R.id.gear);
		display = (TextView) findViewById(R.id.tV);
		nowPlaying = (TextView) findViewById(R.id.tPlay);
		gearState = (TextView) findViewById(R.id.gearState);
		Dipper = (ImageView) findViewById(R.id.dipper);
		Maps = (ImageView) findViewById(R.id.play);
		//ipAddress = "192.168.1.5";
		IP = (Button) findViewById(R.id.connect);
		bMap = BitmapFactory.decodeResource(getResources(), R.drawable.drive);
		IP.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ipAddress = ipaddress.getText().toString();
				display.setText(ipAddress);
			}
		});

		Maps.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (v == Maps) {
					if (song_state == 0) {
						Maps.setImageResource(R.drawable.pause);
						State[0] = 'M';
						nowPlaying.setText("Now Playing: Maps");
						song_state = 1;
					} else if (song_state == 1) {
						Maps.setImageResource(R.drawable.play);
						State[0] = 'N';
						nowPlaying.setText("");
						song_state = 0;
					}
				}

				MyClientTask myClientTask = new MyClientTask(ipAddress,
						socketPort, State);
				myClientTask.execute();

			}
		});

		Dipper.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v == Dipper) {
					if (dipper_state == 0) {
						Dipper.setImageResource(R.drawable.dipper_on);
						State[3] = '1';
						dipper_state = 1;
					} else if (dipper_state == 1) {
						Dipper.setImageResource(R.drawable.dipper_off);
						State[3] = '0';
						dipper_state = 0;
					}

					MyClientTask myClientTask = new MyClientTask(ipAddress,
							socketPort, State);
					myClientTask.execute();

				}
			}
		});

		Gear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (v == Gear) {
					if (FR_State == 0) {
						Gear.setImageResource(R.drawable.reverse);
						gearState.setText("Reverse");
						FR_State = 1;
					} else if (FR_State == 1) {
						Gear.setImageResource(R.drawable.drive);
						gearState.setText("Forward");
						FR_State = 0;
					}
				}
			}
		});

		Gas.setOnTouchListener(new View.OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:

					if (FR_State == 0) {
						display.setText("Accelerating");
						v.setPressed(true);
						State[0] = 'R';
					} else if (FR_State == 1) {
						display.setText("Reverse");
						v.setPressed(true);
						State[0] = 'F';
					}
					MyClientTask myClientTask = new MyClientTask(ipAddress,
							socketPort, State);
					myClientTask.execute();
					break;
				case MotionEvent.ACTION_UP:
					display.setText("Nothing");
					v.setPressed(false);
					State[0] = 'S';
					myClientTask = new MyClientTask(ipAddress, socketPort,
							State);
					myClientTask.execute();
					break;

				}
				return true;
			}
		});

		Brakes.setOnTouchListener(new View.OnTouchListener() {

			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					display.setText("Slowing Down");
					v.setPressed(true);
					State[0] = 'S';
					MyClientTask myClientTask = new MyClientTask(ipAddress,
							socketPort, State);
					myClientTask.execute();
					break;
					
				case MotionEvent.ACTION_UP:
					display.setText("Nothing");
					v.setPressed(false);
				

				}
				return true;
			}
		});
	}

	public class MyClientTask extends AsyncTask<Void, Void, Void> {

		String dstAddress;
		int dstPort;
		//String msgToServer;

		byte[] msgToServer;
		MyClientTask(String addr, int port, byte[] msg) {
			dstAddress = addr;
			dstPort = port;
			msgToServer = msg;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			Socket socket = null;
			DataOutputStream dOut = null;
			DataInputStream dIn = null;

			try {
				socket = new Socket(dstAddress, dstPort);
				dIn = new DataInputStream(socket.getInputStream());
				dOut = new DataOutputStream(socket.getOutputStream());

				dOut.write(msgToServer);

				//dOut.writeUTF(msgToServer);

			} catch (UnknownHostException e) {
				e.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();

			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (dOut != null) {
					try {
						dOut.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (dIn != null) {
					try {
						dIn.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// display.setText(response);
			super.onPostExecute(result);
		}

	}
	
	
	int previous_y = 0;
	String currentState = null;
	String previousState = null;

	@Override
	public void onSensorChanged(SensorEvent e) {
		// TODO Auto-generated method stub
		//int center[] = {-2,-1,0,1,2};
		//int right[] = {3,4,5,6};
		//int left[] = {-6,-5,-4,-3};
		
		//int x = (int) Math.floor(e.values[0]);
		int y = (int) Math.floor(e.values[1]);
		//int z = (int) Math.floor(e.values[2]);
		//StringBuilder Text = new StringBuilder("x = ").append(x)
				//.append(", y = ").append(y).append(", z = ").append(z);

		//acc.setText(Text.toString());
		
		
		//boolean change = false;

		if (y > 0) {
			// String bState = "TL";
			if (y >= 0 && y < 2) {
				State[1] = 'S';
				State[2] = '0';
				previousState = currentState;
				currentState= "straight";
				
			} else if (y >2 && y <= 10) {
				State[1] = 'L';
				State[2] = '2';
				previousState = currentState;
				currentState = "right";
			}
			if (previousState != currentState){
				MyClientTask myClientTask = new MyClientTask(ipAddress,
						socketPort, State);
				myClientTask.execute();
			}
			
			

			/*if (previous_y != y) {
				MyClientTask myClientTask = new MyClientTask(ipAddress,
						socketPort, State);
				myClientTask.execute();
			}

			previous_y = y;*/
			
		}

		else if (y < 0 ) {
			// String bState = "TR";
			if (y <= 0 && y > -2) {
				State[1] = 'S';
				State[2] = '0';
				previousState = currentState;
				currentState= "straight";
				
			} else if (y < -2 && y >= -10) {
				State[1] = 'W';
				State[2] = '2';				
				previousState = currentState;
				currentState= "left";
			} 
			
			if (previousState != currentState){
				MyClientTask myClientTask = new MyClientTask(ipAddress,
						socketPort, State);
				myClientTask.execute();
	
			}

			
		/*	
			if (previous_y != y) {
				MyClientTask myClientTask = new MyClientTask(ipAddress,
						socketPort, State);

				myClientTask.execute();
			}

			previous_y = y;*/
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
}
