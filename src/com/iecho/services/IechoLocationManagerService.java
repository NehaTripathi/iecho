package com.iecho.services;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.main.R;
import com.iecho.webservice.WebServiceConnection;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

public class IechoLocationManagerService extends Service{
	private LocationManager mLocationManager;
	// UI handler codes.
	private static final int UPDATE_ADDRESS = 1;
	private static final int UPDATE_LATLNG = 2;

	private static final int TEN_SECONDS = 10000;
	private static final int TEN_METERS = 10;
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private Handler mHandler;
	private boolean mGeocoderAvailable;
	public Thread serviceThread;
	private boolean flag = true;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mGeocoderAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Geocoder.isPresent();
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case UPDATE_ADDRESS:
					//mAddress.setText((String) msg.obj);
					//Toast.makeText(getApplicationContext(), ">>>>> "+(String) msg.obj, Toast.LENGTH_SHORT).show();
					break;
				case UPDATE_LATLNG:
					//mLatLng.setText((String) msg.obj);
					//Toast.makeText(getApplicationContext(), ">>>>> "+(String) msg.obj, Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};
		setup();

		serviceThread=new Thread(){
			@Override
			public void run() {
				while(flag){
					try {
						new WebServiceConnection("UPDATE_USER_LAT_LONG", "http://tempuri.org/IiEchoMobileService/UpdateFindMeTrackingInformation", "http://tempuri.org/", "UpdateFindMeTrackingInformation", ApplicationUtility.SERVICE_URL, "customerId", "latitude", "longitude", ApplicationUtility.USER_ID, mLatitude+"", mLongitude+"");
						Thread.sleep(30000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		serviceThread.start();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("===>>>  destroying service >>>>>>>>>>>>>>>>");
		flag = false;
	}


	private void setup() {
		Location gpsLocation = null;
		Location networkLocation = null;
		mLocationManager.removeUpdates(listener);

		// Get fine location updates only.
		// Get coarse and fine location updates.
		// Request updates from both fine (gps) and coarse (network) providers.
		gpsLocation = requestUpdatesFromProvider(LocationManager.GPS_PROVIDER, R.string.not_support_gps);
		networkLocation = requestUpdatesFromProvider(LocationManager.NETWORK_PROVIDER, R.string.not_support_network);

		// If both providers return last known locations, compare the two and use the better
		// one to update the UI.  If only one provider returns a location, use it.
		if (gpsLocation != null && networkLocation != null) {
			updateUILocation(getBetterLocation(gpsLocation, networkLocation));
		} else if (gpsLocation != null) {
			updateUILocation(gpsLocation);
		} else if (networkLocation != null) {
			updateUILocation(networkLocation);
		}

	}

	protected Location getBetterLocation(Location newLocation, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return newLocation;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved.
		if (isSignificantlyNewer) {
			return newLocation;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return currentBestLocation;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return newLocation;
		} else if (isNewer && !isLessAccurate) {
			return newLocation;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return newLocation;
		}
		return currentBestLocation;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	private Location requestUpdatesFromProvider(final String provider, final int errorResId) {
		Location location = null;
		if (mLocationManager.isProviderEnabled(provider)) {
			mLocationManager.requestLocationUpdates(provider, TEN_SECONDS, TEN_METERS, listener);
			location = mLocationManager.getLastKnownLocation(provider);
		} else {
			Toast.makeText(this, errorResId, Toast.LENGTH_LONG).show();
		}
		return location;
	}

	private void updateUILocation(Location location) {
		// We're sending the update to a handler which then updates the UI with the new
		// location.
		Message.obtain(mHandler,UPDATE_LATLNG,location.getLatitude() + ", " + location.getLongitude()).sendToTarget();
		mLatitude = location.getLatitude();
		mLongitude = location.getLongitude();
		// Bypass reverse-geocoding only if the Geocoder service is available on the device.
		if (mGeocoderAvailable) doReverseGeocoding(location);
	}

	public static double mLatitude;
	public static double mLongitude;

	private void doReverseGeocoding(Location location) {
		// Since the geocoding API is synchronous and may take a while.  You don't want to lock
		// up the UI thread.  Invoking reverse geocoding in an AsyncTask.

		// (new ReverseGeocodingTask(this)).execute(new Location[] {location});
	}

	private class ReverseGeocodingTask extends AsyncTask<Location, Void, Void> {
		Context mContext;

		public ReverseGeocodingTask(Context context) {
			super();
			mContext = context;
		}

		@Override
		protected Void doInBackground(Location... params) {
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

			Location loc = params[0];
			List<Address> addresses = null;
			try {
				addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
			} catch (IOException e) {
				e.printStackTrace();
				// Update address field with the exception.
				Message.obtain(mHandler, UPDATE_ADDRESS, e.toString()).sendToTarget();
			}
			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get(0);
				// Format the first line of address (if available), city, and country name.
				String addressText = String.format("%s, %s, %s",
						address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
								address.getLocality(),
								address.getCountryName());
				// Update address field on UI.
				Message.obtain(mHandler, UPDATE_ADDRESS, addressText).sendToTarget();
			}
			return null;
		}
	}

	private final LocationListener listener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// A new location update is received.  Do something useful with it.  Update the UI with
			// the location update.
			updateUILocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

}
