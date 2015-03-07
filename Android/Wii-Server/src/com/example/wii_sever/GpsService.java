package com.example.wii_sever;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class GpsService extends Service implements LocationListener {

	Double longitude, latitude;
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Toast.makeText(this,
				"longitude=" + 0 + " latitude=" + 1,
				Toast.LENGTH_LONG).show();
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		//LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
				//0, this);
		 Log.d("ID", "Y");
		Toast.makeText(this,
				"longitude=" + 0 + " latitude=" + 1,
				Toast.LENGTH_LONG).show();
			/*HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://www.URL.com/yourpage.php");
			//This is the data to send
			String MyName =  "0.2" ; //any data to send

			try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("action", MyName));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request

			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String response = httpclient.execute(httppost, responseHandler);

			//This is the response from a php application
			String reverseString = response;
			Toast.makeText(this, "response" + reverseString, Toast.LENGTH_LONG).show();

			} catch (ClientProtocolException e) {
			Toast.makeText(this, "CPE response " + e.toString(), Toast.LENGTH_LONG).show();
			// TODO Auto-generated catch block
			} catch (IOException e) {
			Toast.makeText(this, "IOE response " + e.toString(), Toast.LENGTH_LONG).show();
			// TODO Auto-generated catch block
			}
				
			*/

		
		return START_STICKY;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (location != null){
		longitude = location.getLongitude();
		latitude = location.getLatitude();
		Toast.makeText(this,
				"longitude:" + longitude + " latitude:" + latitude,
				Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}

