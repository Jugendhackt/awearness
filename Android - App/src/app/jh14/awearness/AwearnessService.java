package app.jh14.awearness;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;

import android.util.Log;

import android.widget.Toast;

public class AwearnessService extends Service {

	private LocationListener locationListener;
	
	MainActivity main;
	
	Thread ledThread = null;
	
	private RFduinoService rfDuinoService;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public void setMain(MainActivity main) {
		this.main = main;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_LONG).show();
		return START_STICKY;
	}

	@Override
	public void onCreate() {
		if(ledThread == null) {
			ledThread = new Thread(new LEDThread(rfDuinoService));
			ledThread.start();
		} else if(!ledThread.isAlive()) {
			ledThread = new Thread(new LEDThread(rfDuinoService));
			ledThread.start();
		}
		
		this.rfDuinoService = BluetoothInformations.rfDuinoService;
		
		Log.d("AwearnessService", "Service started");
		
		LocationManager lm = (LocationManager) getApplicationContext()
				.getSystemService(LOCATION_SERVICE);

		LocationProvider provider = lm.getProvider("gps");
		lm.requestLocationUpdates("gps", 60000, // 1min
				1, // 10m
				locationListener);
		
		locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				String stringLatitude = String.valueOf(location.getLatitude());
				String stringLongitude = String.valueOf(location.getLongitude());
				
				//TODO compare latitude and longitude with coordinates with cameras
				
				
				//Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
				//vibrator.vibrate(1000);
			}

			@Override
			public void onProviderDisabled(String arg0) {
				// nothing to do ;(
			}

			@Override
			public void onProviderEnabled(String arg0) {
				// nothing to do ;(
			}

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// nothing to do ;(
			}
			
		};
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("AwearnessService", "AwearnesService destroyed");
	}

}
