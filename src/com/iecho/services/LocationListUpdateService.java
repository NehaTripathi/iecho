package com.iecho.services;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.webservice.WebServiceConnection;

public class LocationListUpdateService extends Service{
	private boolean flag=true;
	private WebServiceConnection locationNamesServiceConnection,currentLocationNameServiceConnection;
	private Handler locationNamesHandler;
	private HashMap<String, String> currentLocationResponseHashMap;
	private JSONObject locationResponseJsonObject;
	public static String[] locationNames;
	public static String[] startingLocationNames;
	public static String locationNameStatus="",locationMsg="",currentLocationName=""; 
	private int hitValue = 0;
	public static long locationRefreshTime = 600000;
	private SharedPreferences refreshTimeSharedPreferences;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		locationNamesHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				try {
					System.out.println("===>>>  locationNamesServiceConnection object : "+locationNamesServiceConnection);
					if (currentLocationNameServiceConnection!=null) {
						getCurrentLocationName();
						//getLocationsNames();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		refreshTimeSharedPreferences = getSharedPreferences("location_refresh_rate"+ApplicationUtility.USER_ID, MODE_PRIVATE);
		locationRefreshTime = refreshTimeSharedPreferences.getLong("time", 600000);
		
		new Thread(){
			public void run() {
				while (flag) {
					try{
						currentLocationNameServiceConnection = new WebServiceConnection("GET_CURRENT_LOCATION_NAME", "http://tempuri.org/IiEchoMobileService/GetCurrentLocation", "http://tempuri.org/", "GetCurrentLocation", ApplicationUtility.SERVICE_URL,"customerID", ApplicationUtility.USER_ID);
						//locationNamesServiceConnection = new WebServiceConnection("GET_ALL_LOCATIONS_NAMES", "http://tempuri.org/IiEchoMobileService/GetGeoLocation", "http://tempuri.org/", "GetGeoLocation", ApplicationUtility.SERVICE_URL,"customerID","range", ApplicationUtility.USER_ID,"20");
						locationNamesHandler.sendEmptyMessage(0);
						if(hitValue < 5){
							hitValue++;
							Thread.sleep(1000 * 30);
							System.out.println("===>>>  thread sleeping ====================== "+hitValue);
						}else{
							System.out.println("===>>>  thread sleeping ====================== "+hitValue);
							Thread.sleep(locationRefreshTime);
						}
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		hitValue=0;
		flag=false;
	}



	private void getCurrentLocationName(){
		currentLocationResponseHashMap=currentLocationNameServiceConnection.serviceResponce();
		if(currentLocationResponseHashMap!=null){
			locationNameStatus = currentLocationResponseHashMap.get("status");
			locationMsg = currentLocationResponseHashMap.get("statusMsg");
			currentLocationName = currentLocationResponseHashMap.get("location");
			System.out.println("===>>>  locationNameStatus : "+locationNameStatus);
			System.out.println("===>>>  locationMsg : "+locationMsg);
			System.out.println("===>>>  currentLocationName : "+currentLocationName);
		}
	}


	private void getLocationsNames(){
		locationResponseJsonObject = locationNamesServiceConnection.jsonResponse();
		System.out.println("===>>>  locationResponseJsonObject >>>>>>>>>>>>>>> "+locationResponseJsonObject);
		if (locationResponseJsonObject!=null) {
			JSONArray aryJSONStrings;
			try {
				aryJSONStrings = new JSONArray(locationResponseJsonObject.getString("GeoLocation"));
				locationNames = new String[aryJSONStrings.length()];
				startingLocationNames = new String[aryJSONStrings.length()+1];
				startingLocationNames[0] = currentLocationName;
				for (int i = 0; i < aryJSONStrings.length(); i++) {
					JSONObject jsonObject = aryJSONStrings.getJSONObject(i);
					try{
						Log.i("Location Name >>>>> ", jsonObject.getString("Name"));
						locationNames[i] = jsonObject.getString("Name");
						startingLocationNames[i+1] = jsonObject.getString("Name");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				String startBuilred = new String();
				for (int i = 0; i < startingLocationNames.length; i++) {
					startBuilred = startBuilred.concat(startingLocationNames[i]).concat("||");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
