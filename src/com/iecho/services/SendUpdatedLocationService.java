package com.iecho.services;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.webservice.WebServiceConnection;

public class SendUpdatedLocationService extends Service{
	public Thread serviceThread;
	//	private WebServiceConnection serviceConnection;
	private double latitude= 37.421535; //28.619699;
	private double longitude= -122.085375;  //77.379740;
	private String _lat,_long;
	private LocationManager locationManager;
	private LocationListener locationListner;
	private boolean flag=true;
	public static String CURRENT_LOCATION_NAME="";
	private SharedPreferences locationSharedPreferences;
	//	private String locationProvider = "";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		locationSharedPreferences = getSharedPreferences("current_location_pref"+ApplicationUtility.USER_ID, MODE_PRIVATE);
		_lat = locationSharedPreferences.getString("lat", "37.421535");
		_long = locationSharedPreferences.getString("long", "-122.085375");
		try{
			latitude = Double.parseDouble(_lat);
			longitude = Double.parseDouble(_long);
		}catch (Exception e) {
			e.printStackTrace();
		}
		fetchGpsLoc(this);

		serviceThread=new Thread(){
			@Override
			public void run() {
				while(flag){
					try {
						new WebServiceConnection("UPDATE_USER_LAT_LONG", "http://tempuri.org/IiEchoMobileService/UpdateFindMeTrackingInformation", "http://tempuri.org/", "UpdateFindMeTrackingInformation", ApplicationUtility.SERVICE_URL, "customerId", "latitude", "longitude", ApplicationUtility.USER_ID, latitude+"", longitude+"");
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
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		flag=true;
		System.out.println("===>>>  inside service on start >>>>>>>>>>>>>");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("===>>>  destroying service >>>>>>>>>>>>>>>>");
		CURRENT_LOCATION_NAME = "";
		flag=false;
	}

	public void fetchGpsLoc(Context ctx) {

		locationManager = (LocationManager)ctx.getSystemService(LOCATION_SERVICE);
		locationListner = new Mylocationlistener();

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			new Thread(){
				public void run() {
					new WebServiceConnection("WRITE_USER_GPS_LOGS", "http://tempuri.org/IiEchoMobileService/WriteGPSLog", "http://tempuri.org/", "WriteGPSLog", ApplicationUtility.SERVICE_URL, "Id", "timestame", "msg", ApplicationUtility.USER_ID, System.currentTimeMillis()+"", "-- provider enebled is gps --");
				};
			}.start();			
		}else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			new Thread(){
				public void run() {
					new WebServiceConnection("WRITE_USER_GPS_LOGS", "http://tempuri.org/IiEchoMobileService/WriteGPSLog", "http://tempuri.org/", "WriteGPSLog", ApplicationUtility.SERVICE_URL, "Id", "timestame", "msg", ApplicationUtility.USER_ID, System.currentTimeMillis()+"", "-- provider enebled is network --");
				};
			}.start();
		} else {
			new Thread(){
				public void run() {
					new WebServiceConnection("WRITE_USER_GPS_LOGS", "http://tempuri.org/IiEchoMobileService/WriteGPSLog", "http://tempuri.org/", "WriteGPSLog", ApplicationUtility.SERVICE_URL, "Id", "timestame", "msg", ApplicationUtility.USER_ID, System.currentTimeMillis()+"", "-- no provider is enabled --");
				};
			}.start();
		}

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , 1000 , 0 , locationListner);
	}

	private class Mylocationlistener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
//			if (location != null) {
				longitude = location.getLongitude();
				latitude = location.getLatitude();
				System.out.println("===>>>  >>>>>>>>>>>>>> "+longitude);
				System.out.println("===>>>  >>>>>>>>>>>>> "+latitude);

				new Thread(){
					public void run() {
						new WebServiceConnection("WRITE_USER_GPS_LOGS", "http://tempuri.org/IiEchoMobileService/WriteGPSLog", "http://tempuri.org/", "WriteGPSLog", ApplicationUtility.SERVICE_URL, "Id", "timestame", "msg", ApplicationUtility.USER_ID, System.currentTimeMillis()+"", "latitude and longilude >>>>> "+latitude+" , "+longitude+" respectively");
					};
				}.start();	
				
				try{
					Editor editor = locationSharedPreferences.edit();
					editor.putString("lat", latitude+"");
					editor.putString("long", longitude+"");
					editor.commit();
				}catch (Exception e) {
					e.printStackTrace();
				}
				Geocoder gcd = new Geocoder(SendUpdatedLocationService.this, Locale.getDefault());
				List<Address> addresses;
				try {
					addresses = gcd.getFromLocation(latitude, longitude, 1);
					if (addresses.size() > 0)
						CURRENT_LOCATION_NAME = addresses.get(0).getLocality();
					System.out.println("===>>>  Location name is :"+ SendUpdatedLocationService.CURRENT_LOCATION_NAME);
				} catch (IOException e) {
					e.printStackTrace();
				}
//			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> onProviderDisabled >>>>>>>>>>>>>>>>>>>>>> "+provider);
			final String pro = provider;
			new Thread(){
				public void run() {
					new WebServiceConnection("WRITE_USER_GPS_LOGS", "http://tempuri.org/IiEchoMobileService/WriteGPSLog", "http://tempuri.org/", "WriteGPSLog", ApplicationUtility.SERVICE_URL, "Id", "timestame", "msg", ApplicationUtility.USER_ID, System.currentTimeMillis()+"", "-- onProviderDisabled() -->"+pro);
				};
			}.start();
		}

		@Override
		public void onProviderEnabled(String provider) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> onProviderEnabled >>>>>>>>>>>>>>>>>>>>>> "+provider);
			final String pro = provider;
			new Thread(){
				public void run() {
					new WebServiceConnection("WRITE_USER_GPS_LOGS", "http://tempuri.org/IiEchoMobileService/WriteGPSLog", "http://tempuri.org/", "WriteGPSLog", ApplicationUtility.SERVICE_URL, "Id", "timestame", "msg", ApplicationUtility.USER_ID, System.currentTimeMillis()+"", "-- onProviderEnabled() -->"+pro);
				};
			}.start();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> onStatusChanged >>>>>>>>>>>>>>>>>>>>>> "+provider);
			final String pro = provider;
			new Thread(){
				public void run() {
					new WebServiceConnection("WRITE_USER_GPS_LOGS", "http://tempuri.org/IiEchoMobileService/WriteGPSLog", "http://tempuri.org/", "WriteGPSLog", ApplicationUtility.SERVICE_URL, "Id", "timestame", "msg", ApplicationUtility.USER_ID, System.currentTimeMillis()+"", "-- onStatusChanged() -->"+pro);
				};
			}.start();
		}
	}
}
