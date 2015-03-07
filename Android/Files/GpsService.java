package com.log.wiicar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class GpsService extends Service implements LocationListener {

	Double longitude, latitude;
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
				0, this);
		Toast.makeText(this,
				"longitude=" + longitude + " latitude=" + latitude,
				Toast.LENGTH_LONG).show();

		
		return super.onStartCommand(intent, flags, startId);
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
