package com.log.wiicar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;




public class driving extends Activity implements SensorEventListener {

	private SensorManager sensorManager;
	private Sensor senAccel;
	InetAddress serveraddress;
	EditText ipAdd;
	ConnectIp test ;
	String ipAddress = test.ip;
	String bState = null;
	Button Left, Right, Maps;
	Button Gas, Brakes, Dipper, FR;
	static TextView display, acc;
	int socketPort = 8888;
	// byte[] State = { 'S', 'S', '0', '0', '\r' };
	byte State = 'C';
	int dipperState = 0;
	int songState = 0;
	static int FR_State = 1;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.driving);

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		senAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, senAccel,
				SensorManager.SENSOR_DELAY_NORMAL);
		Gas = (Button) findViewById(R.id.bGas);
		Brakes = (Button) findViewById(R.id.bBrakes);
		Dipper = (Button) findViewById(R.id.dipper);
		FR = (Button) findViewById(R.id.gear);
		Maps = (Button) findViewById(R.id.maps);
		Left = (Button) findViewById(R.id.left);
		Right = (Button) findViewById(R.id.right);
		display = (TextView) findViewById(R.id.tV);
		//acc = (TextView) findViewById(R.id.accText);
		// ipAdd =(EditText) findViewById(R.id.address);
		ipAddress = "192.16.4.5";
		
		

		// textIn = (TextView) findViewById(R.id.textIn)

		/*State = 'M';
		MyClientTask myClientTask = new MyClientTask(ipAddress, socketPort,
				State);
		myClientTask.execute();*/

		Dipper.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (v == Dipper) {
					if (dipperState == 0) {
						//Dipper.setImageResource(R.drawable.dipper_on);
						dipperState = 1;
						State = 'I';
					} else if (dipperState == 1) {
						//Dipper.setImageResource(R.drawable.dipper_off);
						dipperState = 0;
						State = 'J';
					}

					MyClientTask myClientTask = new MyClientTask(ipAddress,
							socketPort, State);
					myClientTask.execute();
					
				}
			}
		});

		Maps.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (songState == 0) {
					State = 'K';
					songState = 1;
				} else if (songState == 1) {
					State = 'L';
					songState = 0;
				}

				MyClientTask myClientTask = new MyClientTask(ipAddress,
						socketPort, State);
				myClientTask.execute();

			}
		});

		FR.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v == FR) {
					if (FR_State == 0) {
						//FR.setImageResource(R.drawable.f);
						FR_State = 1;
					} else if (FR_State == 1) {
						//FR.setImageResource(R.drawable.rev);
						FR_State = 0;
					}
				}
			}
		});
		Gas.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// send the message to the server
				if (FR_State == 0) {
					display.setText("Reverse");
					State = 'B';
					//FR_State = 1;
				} else if (FR_State == 1) {
					display.setText("Accelerating");
					State = 'A';
					//FR_State = 0;
				}

				MyClientTask myClientTask = new MyClientTask(ipAddress,
						socketPort, State);

				myClientTask.execute();

			}
		});
		
		Gas.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					// send the message to the server
					if (FR_State == 0) {
						display.setText("Reverse");
						State = 'B';
						//FR_State = 1;
					} else if (FR_State == 1) {
						display.setText("Accelerating");
						State = 'A';
						//FR_State = 0;
					}

					MyClientTask myClientTask = new MyClientTask(ipAddress,
							socketPort, State);

					myClientTask.execute();
				}
				if(event.getAction() == MotionEvent.ACTION_UP)
				{
					display.setText("Let's Drive" );
					State = 'C';
					
					MyClientTask myClientTask = new MyClientTask(ipAddress,
							socketPort, State);

					myClientTask.execute();
				}
				
				
				return true;
			}
		});

		Brakes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				display.setText("Slowing down");
				State = 'C';

				MyClientTask myClientTask = new MyClientTask(ipAddress,
						socketPort, State);

				myClientTask.execute();

			}
		});

		Left.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				display.setText("Turning Left");
				State = 'E';

				MyClientTask myClientTask = new MyClientTask(ipAddress,
						socketPort, State);

				myClientTask.execute();

			}
		});

		Right.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				display.setText("Turning Right");
				State = 'G';

				MyClientTask myClientTask = new MyClientTask(ipAddress,
						socketPort, State);

				myClientTask.execute();

			}
		});

		/*
		 * MyClientTask myClientTask = new MyClientTask(ipAddress, socketPort,
		 * State);
		 * 
		 * myClientTask.execute();
		 */

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		MyClientTask myClientTask;
		if (songState == 1) {
			State = 'L';
			songState = 0;
			myClientTask = new MyClientTask(ipAddress, socketPort, State);
			myClientTask.execute();
		}
		State = 'C';

		myClientTask = new MyClientTask(ipAddress, socketPort, State);
		myClientTask.execute();
	}

	@Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();

		MyClientTask myClientTask;
		if (songState == 1) {
			State = 'L';
			songState = 0;
			myClientTask = new MyClientTask(ipAddress, socketPort, State);
			myClientTask.execute();
		}
		State = 'C';

		myClientTask = new MyClientTask(ipAddress, socketPort, State);
		myClientTask.execute();
	}

	public class MyClientTask extends AsyncTask<Void, Void, Void> {

		String dstAddress;
		int dstPort;
		// String msgToServer;
		byte msgToServer;

		MyClientTask(String addr, int port, byte msg) {
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

				dOut.writeByte(msgToServer);
				
				// dOut.write(msgToServer);

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
		
		int x = (int) Math.floor(e.values[0]);
		int y = (int) Math.floor(e.values[1]);
		int z = (int) Math.floor(e.values[2]);
		StringBuilder Text = new StringBuilder("x = ").append(x)
				.append(", y = ").append(y).append(", z = ").append(z);

		acc.setText(Text.toString());
		
		
		//boolean change = false;

		if (x > -1 && z > 5 && y > 0) {
			// String bState = "TL";
			if (y >= 0 && y < 2) {
				State = 'H';
				previousState = currentState;
				currentState= "straight";
				
			} else if (y >2 && y <= 6) {
				State = 'E';
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

		else if (x > -1 && y < 0 && z > 5) {
			// String bState = "TR";
			if (y <= 0 && y > -2) {
				State = 'H';
				previousState = currentState;
				currentState= "straight";
				
			} else if (y < -2 && y >= -5) {
				State = 'G';
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

	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this);
	}

	protected void onResume() {
		super.onResume();
		sensorManager.registerListener(this, senAccel,
				SensorManager.SENSOR_DELAY_NORMAL);
	}
}
