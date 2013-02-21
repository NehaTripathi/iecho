package com.iecho.main;

import java.util.HashMap;

import android.app.Activity;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.webservice.WebServiceConnection;

public class GetAllUsersCurrentState {
	private WebServiceConnection webServiceConnection;
	private HashMap<String, String> responseHashMap;
	private String serviceStatus;
	private String[] friendsIDsArray;
	private String userStatus;
	private String userStateMsg;
	private String friendCurrentStatus = "";
	public static String[] friendStatusInfoArray;
	public static HashMap<String, String> hashMap = new HashMap<String, String>();

	public GetAllUsersCurrentState(Activity activity, String[] userIDsArrayList) {
		try {
			friendsIDsArray = userIDsArrayList;
			friendStatusInfoArray = new String[userIDsArrayList.length];
			getUserCurrentStatus();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getUserCurrentStatus() {
		new Thread() {
			@Override
			public void run() {
				try {
					for (int i = 0; i < friendStatusInfoArray.length; i++) {
						try {
							webServiceConnection = new WebServiceConnection(
									"USER_CURRENT_STATE",
									"http://tempuri.org/IiEchoMobileService/GetUserCurrentState",
									"http://tempuri.org/",
									"GetUserCurrentState",
									ApplicationUtility.SERVICE_URL,
									"customerId", friendsIDsArray[i]);
							getResponseFromService();
							System.out.println("friend current status is : "+friendCurrentStatus);
							friendStatusInfoArray[i] = friendCurrentStatus;
							hashMap.put(friendsIDsArray[i], friendCurrentStatus);
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

	private synchronized void getResponseFromService() {
		try {
			responseHashMap = webServiceConnection.serviceResponce();
			if (responseHashMap != null) {
				userStatus = responseHashMap.get("status");
				userStateMsg = responseHashMap.get("statusMsg");
				if (userStatus.equals("true")) {
					if (userStateMsg.equals("Public")) {
						friendCurrentStatus = "Public";
					} else if (userStateMsg.equals("Private")) {
						friendCurrentStatus = "Private";
					} else if (userStateMsg.equals("Secret")) {
						friendCurrentStatus = "Secret";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}