package com.iecho.main;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.webservice.WebServiceConnection;

public class GetAllUserLocations {
	private Activity mActivity;
	private WebServiceConnection webServiceConnection;
	private HashMap<String, String> responseHashMap;
	private String serviceStatus;
	private String userLatitude;
	private String userLongitude;
	private String addressLine;
	private String countryName;
	public static String[] friendLocationInfoArray;
	private String userAddressDetails = "";
	private String[] friendsIDsArray;

	public GetAllUserLocations(Activity activity,String[] userIDsArrayList) {
		try {
			mActivity = activity;
			friendsIDsArray = userIDsArrayList;
			friendLocationInfoArray = new String[userIDsArrayList.length];
			getUserLatLong();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void getUserLatLong() {
		new Thread() {
			@Override
			public void run() {
				try {
					for (int i = 0; i < friendLocationInfoArray.length; i++) {
						try {
							webServiceConnection = new WebServiceConnection(
									"GET_FRIEND_UPDATED_LOCATION",
									"http://tempuri.org/IiEchoMobileService/GetFindMeTrackingInformation",
									"http://tempuri.org/", "GetFindMeTrackingInformation",
									ApplicationUtility.SERVICE_URL, "customerId", friendsIDsArray[i]);
							getResponseFromService();
							friendLocationInfoArray[i] = userAddressDetails;
							Thread.sleep(500);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private synchronized void getResponseFromService(){
		try {
			responseHashMap = webServiceConnection.serviceResponce();
			serviceStatus = responseHashMap.get("status");
			if (serviceStatus.equals("true")) {
				userLatitude = responseHashMap.get("latitude");
				userLongitude = responseHashMap.get("longitude");
				System.out.println("userLatitude "+userLatitude+"  userLongitude  "+userLongitude);
				getLocationDetails(userLatitude, userLongitude);
			} else {
				System.out.println("not able to fetch user location");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getLocationDetails(String latitude,String longitude) {
		Geocoder geocoder = new Geocoder(mActivity);
		try {
			List<Address> fromLocation = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
			if (fromLocation != null) {
				addressLine = fromLocation.get(0).getAddressLine(0);
				countryName = fromLocation.get(0).getCountryName();
				userAddressDetails = addressLine+", "+countryName;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return userAddressDetails;
	}
}