package com.example.wii_sever;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;

import android.view.View;
import android.widget.Button;
//import android.support.v7.app.ActionBarActivity;
//import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;


import java.net.Socket;
import java.util.Enumeration;
import java.util.UUID;

import com.example.wii_sever.R;

public class Wii_ServerActivity extends Activity {

	TextView ipAdd, msg;
	ServerSocket serverSocket;
	Button test,gps;
	byte[] testBytes = { 'S', 'L', '2', '1', '\n' };
	//byte testBytes = 'C';
	MediaPlayer song, engine;
	// String bluetoothMsg = "";
	private BluetoothAdapter btAdap = null;
	private BluetoothSocket btSocket = null;
	private OutputStream outStream = null;

	// UUID service - This is the type of Bluetooth device that the BT module is
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// MAC-Address of HC-05
	public String Mac_Address = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wii__server);
		
		
		msg = (TextView) findViewById(R.id.msg);
		ipAdd = (TextView) findViewById(R.id.ipAdd);
		// check = (Button) findViewById(R.id.button);
		test = (Button) findViewById(R.id.test);
		// getting the bluetooth adapter value and calling checkBTstate function

		ipAdd.setText(getIpAddress());

		Thread socketServerThread = new Thread(new SocketServerThread());
		socketServerThread.start();
		
		

		test.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendData(testBytes);
				Toast.makeText(getBaseContext(), "Message Sent",
						Toast.LENGTH_SHORT).show();
			}
		});

		btAdap = BluetoothAdapter.getDefaultAdapter();
		checkBTState();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class SocketServerThread extends Thread {

		static final int socketServerPORT = 8888;
		byte[] state;
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			Socket socket = null;
			DataInputStream dIn = null;
			DataOutputStream dOut = null;

			state = new byte[5];

			try {
				serverSocket = new ServerSocket(socketServerPORT);
				
				while (true) {
					socket = serverSocket.accept();

					dIn = new DataInputStream(socket.getInputStream());
					dOut = new DataOutputStream(socket.getOutputStream());

					/*
					 * System.out.println("ip: " + socket.getInetAddress());
					 * System.out.println("State: " + dIn.readUTF());
					 */
					// state = dIn.readUTF();
					dIn.read(state);
					
					
					// song = MediaPlayer.create(Wii_ServerActivity.this,
					// R.raw.maps);
					if (state[0] == 'K') {
						engine = MediaPlayer.create(Wii_ServerActivity.this,
								R.raw.splashsound);
						engine.start();

						Thread timer = new Thread() {

							public void run() {

								try {
									sleep(5000);							
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								finally{						
									
									engine.stop();
								}

							}
						};
						timer.start();
					}

					
					else if (state[0] == 'M') {
						song = MediaPlayer.create(Wii_ServerActivity.this,
								R.raw.maps);
						song.start();
						
						
					} else if (state[0] == 'N') {
						song.stop();
					} else {

						Wii_ServerActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								//msg.setText("State: " + state[0]+ state[1]+ state[2]+ state[3]);
								msg.setText("State: " + state.length);

								/*
								 * if (state[4] == 'M') {
								 * 
								 * song.start(); state[4] = '\r'; } else if
								 * (state[4] == 'N') { song.stop(); state[4] =
								 * '\r'; }
								 */

								sendData(state);
								Toast.makeText(getBaseContext(),
										"Message Recieved", Toast.LENGTH_SHORT)
										.show();

							}

						});
						// dOut.writeUTF("Hello");
					}
				}
			}

			catch (IOException e) {
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

				if (serverSocket != null) {
					try {
						serverSocket.close();
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

				if (dOut != null) {
					try {
						dOut.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}
	}

	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Intent intent = getIntent();
		Mac_Address = intent
				.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Set up a pointer to the remote device using its address.
		BluetoothDevice device = btAdap.getRemoteDevice(Mac_Address);

		// Attempt to create a bluetooth socket for comms
		try {
			btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			Toast.makeText(getBaseContext(),
					"ERROR - Could not create Bluetooth socket",
					Toast.LENGTH_SHORT).show();
		}

		// Establish the connection

		try {
			btSocket.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				btSocket.close(); // if IO exception catches attempt to close
									// the socket.
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				Toast.makeText(getBaseContext(),
						"ERROR - Could not close Bluetooth socket",
						Toast.LENGTH_SHORT).show();
				// e1.printStackTrace();
			}
			e.printStackTrace();
		}

		// Create a data stream so we can talk to the device
		try {
			outStream = btSocket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			Toast.makeText(getBaseContext(),
					"ERROR - Could not create bluetooth outstream",
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// song.release();

		try {
			btSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getBaseContext(),
					"ERROR - Failed to close Bluetooth socket",
					Toast.LENGTH_SHORT).show();
		}

		// finish();
	}

	// takes the UUID and creates a comms socket
	/*
	 * private BluetoothSocket createBluetoothSocket(BluetoothDevice device)
	 * throws IOException {
	 * 
	 * return device.createRfcommSocketToServiceRecord(MY_UUID); }
	 */
	private void checkBTState() {
		// Check device has Bluetooth and that it is turned on
		if (btAdap == null) {
			Toast.makeText(getBaseContext(),
					"ERROR - Device does not support bluetooth",
					Toast.LENGTH_SHORT).show();
			finish();
		} else {
			if (btAdap.isEnabled()) {
			} else {
				// Prompt user to turn on Bluetooth
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, 1);
			}
		}
	}

	private void sendData(byte[] msg) {
		//byte[] msgBuffer = msg.getBytes();
		// byte[] terminator = { 0xD, 0xA };

		try {
			outStream.write(msg);
			// outStream.write(terminator);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			finish();
		}
	}

	private String getIpAddress() {

		String ip = "";

		try {
			Enumeration<NetworkInterface> enumNetworkInt = NetworkInterface
					.getNetworkInterfaces();

			while (enumNetworkInt.hasMoreElements()) {
				NetworkInterface NetworkInterface = enumNetworkInt
						.nextElement();
				Enumeration<InetAddress> enumInetAddress = NetworkInterface
						.getInetAddresses();
				while (enumInetAddress.hasMoreElements()) {
					InetAddress inetAddress = enumInetAddress.nextElement();

					if (inetAddress.isSiteLocalAddress()) {
						ip += "SiteLocalAddress: "
								+ inetAddress.getHostAddress() + "\n";
					}

				}

			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		return ip;
	}
	public void startSer(View view){
	
		startService(new Intent(getBaseContext(),GpsService.class));
	}

}
