package com.dugbel.glass.propertyvision;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.glass.app.Card;

/**
 * Activity that starts the {@link MainActivity} and passes a {@link Location}
 * object to the {@link PropertyLookupActivity} to be processed
 * 
 * @author Doug Bell (douglas.bell@gmail.com)
 * 
 */
public class MainActivity extends Activity{

	/** The {@link Log} tag */
	private static final String TAG = MainActivity.class.getSimpleName();

	/** Time in ms to show the title screen */
	private static final int TITLE_SCREEN_TIMOUT_IN_MS = 1500;
	
	/** Minimum accuracy in meters to be valid */
	private static final int MIN_ACCURACY_IN_METERS = Integer.MAX_VALUE; // TODO No limit for now
	
	/** Instance of this context */
	private Context context;

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = this;
		
		setContentView(R.layout.activity_title_screen);

		new Handler().postDelayed(new Runnable() {
			/*
			 * (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

				boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
				Log.d(TAG, "GPS provider is enabled: " + gpsIsEnabled);

				boolean networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
				Log.d(TAG, "Network provider is enabled: " + networkIsEnabled);

				if (!gpsIsEnabled && !networkIsEnabled) {
					final Card card = new Card(context);
					card.setText("GPS and WiFi is currently disabled");
					card.setFootnote("Please activate GPS or WiFi and try again");
					setContentView(card.getView());
					return;
				}

				Location location = locationManager.getLastKnownLocation(gpsIsEnabled ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER);

				float accuracy = location.getAccuracy();
				Log.d(TAG, "Accuracy: " + accuracy + "m");
				if (accuracy <= MIN_ACCURACY_IN_METERS) {
					Intent intent = new Intent(context, PropertyLookupActivity.class);
					intent.putExtra("location", location);
					startActivity(intent);
				} else {
					Card card = new Card(context);
					card.setText("Could not get accurate location lock");
					card.setFootnote("Please move to another location and try again");
					setContentView(card.getView());
				}

				finish();
			}
		}, TITLE_SCREEN_TIMOUT_IN_MS);


	}
}