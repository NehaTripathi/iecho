package com.iecho.main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.database.DataBaseConnectionManager;
import com.iecho.services.IechoLocationManagerService;
import com.iecho.services.LocationListUpdateService;
import com.iecho.services.SendUpdatedLocationService;
import com.iecho.vo.ContactVO;
import com.iecho.webservice.WebServiceConnection;
/**
 * 
 * @author nikhils
 *	This class is the landing home activity which user sees after successful login
 *	This class download all the contacts of the user as well as sync contacts to server
 */
public class HomeActivity extends Activity {
	private Button contactsButton, notificationButton, settingsButton,requestButton;
	private Button friendsButton,sosButton;
	public Context context;
	private SharedPreferences firstLaunch;
	private DataBaseConnectionManager connectionManager;
	private String isWebRegistered="",userId="";
	private ProgressDialog contactSyncProgressDialog,updateProgressDialog;
	private Handler contactHandler,friendListHandler,newHandler,updatePendingFriendsHandler,userStateHandler;
	private WebServiceConnection serviceConnection,userStateServiceConnection;
	private WebServiceConnection checkSubscriptionServiceConnection;
	private JSONObject responseJsonObject;
	private String f_name="",l_name="",number="",type="";
	private String user_id="", email="", image_url="";
	private boolean dbAlreadyExists;
	private boolean checkFirstLaunch;
	private String[] usersId;
	//	public static String[] locationNames;
	//	public static String[] startingLocationNames;
	private String[] fName,lName,contactNumber,emailId;
	private ArrayList<byte[]> imageArrayList;
	private String toDeleteUID="";
	private String userStatus,userStateMsg;
	private HashMap<String, String> statusResponseHashMap;
	//	private Intent locationServiceIntent;
	private ProgressDialog progressDialog;
	private Handler sendSoSHandler,checkSubscriptionHandler,sendSMSFromSIMHandler;
	private HashMap<String, String> sosResponseHashMap,checkSubscriptionHashMap;
	private byte[] sosByteArray;
	private Bitmap sosBitmapImage;
	private int counter=0;
	private String[] allUsersId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		context=this;
		setUpViews();
		imageArrayList=new ArrayList<byte[]>();
//		locationServiceIntent=new Intent(context,SendUpdatedLocationService.class);
		locationServiceIntent=new Intent(context,IechoLocationManagerService.class);
		locationArrayIntent = new Intent(context, LocationListUpdateService.class);
		startService(locationServiceIntent);

		// Thread for getting location names form the server based on the current gps location

		try {
			startService(locationArrayIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Button infoButton=(Button)findViewById(R.id.home_info);
		infoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent infoIntent=new Intent(context, AboutIechoActivity.class);
				startActivity(infoIntent);
			}
		});

		System.out.println("===>>>  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+doesDBExists());
		dbAlreadyExists=doesDBExists();
		connectionManager=new DataBaseConnectionManager(context);

		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		isWebRegistered=bundle.getString("isRegistered");
		userId=bundle.getString("userId");
		ApplicationUtility.USER_ID=userId;
		firstLaunch=getSharedPreferences(ApplicationUtility.DB_NAME, MODE_PRIVATE);
		checkFirstLaunch=firstLaunch.getBoolean("isFirstLaunch", true);
		System.out.println("===>>>  >>>>>>>>>>>>> IS FIRST LAUNCH>>>>>>>>>>>>>>>"+checkFirstLaunch);

		userStateHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				try{
					statusResponseHashMap=userStateServiceConnection.serviceResponce();
					if(statusResponseHashMap!=null){
						userStatus=statusResponseHashMap.get("status");
						userStateMsg=statusResponseHashMap.get("statusMsg");
						if(userStatus.equals("true")){
							if(userStateMsg.equals("Public")){
								ApplicationUtility.USER_CURRENT_STATE="Public";
							}else if(userStateMsg.equals("Private")){
								ApplicationUtility.USER_CURRENT_STATE="Private";
							}else if(userStateMsg.equals("Secret")){
								ApplicationUtility.USER_CURRENT_STATE="Secret";
							}
						}
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			};
		};

		/**
		 * Handler for sending SMS form SIM if there is more than 120 seconds to respond server 
		 */
		sendSMSFromSIMHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				counter=1000;
				if (progressDialog!=null) {
					progressDialog.dismiss();
				}
				Toast.makeText(context, "Sending SMS from SIM", Toast.LENGTH_SHORT).show();
				connectionManager=new DataBaseConnectionManager(context);
				String[] allContects=connectionManager.getContactNumbers();
				try {
					if(allContects!=null){
						for (String allContect : allContects) {
							System.out.println("===>>>  Sending sms to >>>>>> "+allContect);
							if (SendUpdatedLocationService.CURRENT_LOCATION_NAME.equals("")) {
								sendSMSFromSim(allContect, ApplicationUtility.EMERGENCY_SMS);	
							} else {
								System.out.println("===>>>  Sending location name: "+SendUpdatedLocationService.CURRENT_LOCATION_NAME);
								sendSMSFromSim(allContect, "This is an emergency SOS message. I am currently in problem. I am currently at "+SendUpdatedLocationService.CURRENT_LOCATION_NAME);
							}
							Toast.makeText(context, "Sending SMS to "+allContect, Toast.LENGTH_SHORT).show();
						}
					}					
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		};

		contactHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(contactSyncProgressDialog!=null){
					contactSyncProgressDialog.dismiss();
				}
				if(!dbAlreadyExists){
					System.out.println("===>>>  =========||==========||=========");
					insertContacts();
				}else{
					System.out.println("===>>>  =========else==========else=========");
					//connectionManager.deleteWebContects();
					updateContacts();
				}
				updateProgressDialog=ProgressDialog.show(context, "", "updating contacts...");
				try{
					new Thread(){
						@Override
						public void run() {
							serviceConnection=new WebServiceConnection("GET_ALL_FRIENDS_LIST", "http://tempuri.org/IiEchoMobileService/GetFriendList", "http://tempuri.org/", "GetFriendList", ApplicationUtility.SERVICE_URL,"customerId",userId);
							friendListHandler.sendEmptyMessage(0);
						}
					}.start();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		friendListHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(updateProgressDialog!=null){
					updateProgressDialog.dismiss();
				}
				try {
					if(checkFirstLaunch){
						System.out.println("===>>>  inside if==========>>>>>>");
						updateFriendContacts();
						Editor editor=firstLaunch.edit();
						editor.putBoolean("isFirstLaunch", false);
						editor.commit();
					}else{
						System.out.println("===>>>  inside else==========>>>>>>");
						updatePendingFriendsHandler.sendEmptyMessage(0);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

					if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
						System.out.println("services are enabled");
					}else{
						showGPSDisabledAlertToUser();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};

		newHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(updateProgressDialog!=null){
					updateProgressDialog.dismiss();
				}
				try {
					if(checkFirstLaunch){
						System.out.println("===>>>  inside if==========");
						updateFriendContacts();
						Editor editor=firstLaunch.edit();
						editor.putBoolean("isFirstLaunch", false);
						editor.commit();
					}else{
						System.out.println("===>>>  inside else==========");
						updatePendingFriendsHandler.sendEmptyMessage(0);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

					if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
						System.out.println("services are enabled");
					}else{
						showGPSDisabledAlertToUser();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		};

		updatePendingFriendsHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				try{
					checkUpdateFriendContacts();
					for (int i = 0; i < usersId.length; i++) {
						String con_name=connectionManager.checkForIdExists(usersId[i]);
						System.out.println("===>>>  name is: "+con_name);
						if(con_name.equals("")){
							try{
								long rowid=connectionManager.addNewContact(new ContactVO(fName[i], lName[i], contactNumber[i], emailId[i], "Public", "My Group", "0", "1",usersId[i],imageArrayList.get(i)));
								System.out.println("===>>>  contact addad at>>>>>>"+rowid);
							}catch (Exception e) {
								e.printStackTrace();
							}
						}else{
							System.out.println("===>>>  No need to update>>>>>>");
						}
					}
					checkForContactDelete();
				}catch (Exception e) {
					e.printStackTrace();
				}
			};
		};

		/**
		 * Thread for fetching the user current state i.e. Private/Public
		 */
		new Thread(){
			@Override
			public void run() {
				userStateServiceConnection=new WebServiceConnection("USER_CURRENT_STATE", "http://tempuri.org/IiEchoMobileService/GetUserCurrentState", "http://tempuri.org/", "GetUserCurrentState", ApplicationUtility.SERVICE_URL,"customerId",ApplicationUtility.USER_ID);
				userStateHandler.sendEmptyMessage(0);
			};
		}.start();



		/**
		 * Thread for checking whether the user is subscribed or not
		 */
		new Thread(){
			@Override
			public void run() {
				checkSubscriptionServiceConnection=new WebServiceConnection("CHECK_USER_SUBSCRIPTION", "http://tempuri.org/IiEchoMobileService/IsSubscribeCustomer", "http://tempuri.org/", "IsSubscribeCustomer", ApplicationUtility.SERVICE_URL,"CustomerId",ApplicationUtility.USER_ID);
				checkSubscriptionHandler.sendEmptyMessage(0);
			};
		}.start();

		/**
		 * Handler for updating the user subscription status
		 */
		checkSubscriptionHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				try {
					checkSubscriptionHashMap=checkSubscriptionServiceConnection.serviceResponce();
					if(checkSubscriptionHashMap!=null){
						String status=checkSubscriptionHashMap.get("status");
						if (status.equals("true")) {
							ApplicationUtility.IS_SUBSCRIBED_USER="YES";
						} else {
							ApplicationUtility.IS_SUBSCRIBED_USER="NO";
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		/**
		 * Handler that checks whether SMS send by the server on not
		 */
		sendSoSHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if (progressDialog!=null) {
					progressDialog.dismiss();
				}
				sosResponseHashMap=serviceConnection.serviceResponce();
				if (sosResponseHashMap!=null) {

					ApplicationUtility.USER_CURRENT_STATE="Public";
					String status=sosResponseHashMap.get("status");
					String message=sosResponseHashMap.get("statusMsg");
					if (status.equals("true")) {
						counter=1000;
						System.out.println("===>>>  Value of counter >>>>>>>>"+counter);
						Toast.makeText(context, "SMS sent to all friends", Toast.LENGTH_LONG).show();
					} else {
						counter=1000;
						System.out.println("===>>>  Value of counter >>>>>>>>"+counter);
						Toast.makeText(context,message , Toast.LENGTH_LONG).show();
					}
				}
			}
		};


		if (isWebRegistered.equals("1")) {
			System.out.println("===>>>  >>>>>>>>>>>>>>>>>>>> WEB REGISTERED <<<<<<<<<<<<<<<<<<<<<<<");
			contactSyncProgressDialog=ProgressDialog.show(context, "", "please wait...");
			new Thread(){
				@Override
				public void run() {
					serviceConnection=new WebServiceConnection("GET_SERVER_CONTACT_LIST", "http://tempuri.org/IiEchoMobileService/GetWebSiteContactList", "http://tempuri.org/", "GetWebSiteContactList", ApplicationUtility.SERVICE_URL,"customerId",userId);
					contactHandler.sendEmptyMessage(0);
				}
			}.start();
		}else{
			updateProgressDialog=ProgressDialog.show(context, "", "updating contacts...");
			try{
				new Thread(){
					@Override
					public void run() {
						serviceConnection=new WebServiceConnection("GET_ALL_FRIENDS_LIST", "http://tempuri.org/IiEchoMobileService/GetFriendList", "http://tempuri.org/", "GetFriendList", ApplicationUtility.SERVICE_URL,"customerId",userId);
						newHandler.sendEmptyMessage(0);
					}
				}.start();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}


		contactsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent contactIntent=new Intent(context, ContactsActivity.class);
				startActivityForResult(contactIntent, 103);
			}
		});

		friendsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent contectIntent=new Intent(context, FriendLocationMapActivity.class);
				contectIntent.putExtra("REQUEST_NAME", "FriendLocationMapActivity");
				startActivityForResult(contectIntent, 504);
			}
		});

		sosButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ApplicationUtility.IS_SUBSCRIBED_USER.equals("YES")) {
					// this will open camera
					Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(cameraIntent, 1285);
				} else {
					if(counter==0 || counter==1000){
						if (counter==1000) {
							counter=0;
						}
						counterJob();
					}
					if(new ApplicationUtility(context).checkNetworkConnection()){
						Toast.makeText(context, "State changed to Public", Toast.LENGTH_LONG).show();
						progressDialog=ProgressDialog.show(context, "", "please wait...");
						new Thread(){
							@Override
							public void run() {
								serviceConnection=new WebServiceConnection("CHANGE_USER_STATE", "http://tempuri.org/IiEchoMobileService/SetCustomerState", "http://tempuri.org/", "SetCustomerState", ApplicationUtility.SERVICE_URL, "customerId", "state", "hour", ApplicationUtility.USER_ID, "1", "0");
								serviceConnection=new WebServiceConnection("SEND_SOS", "http://tempuri.org/IiEchoMobileService/SOSRequest", "http://tempuri.org/", "SOSRequest", ApplicationUtility.SERVICE_URL, "customerId", "imageByte","CountryCode",  ApplicationUtility.USER_ID, sosByteArray, "1");

								final String[] userIDS=new DataBaseConnectionManager(context).getAllUsersId();
								try{
									for (String element : userIDS) {
										System.out.println("===>>>  Sending request to :"+element);
										new WebServiceConnection("FIND_ME_REQUEST","http://tempuri.org/IiEchoMobileService/FindMe", "http://tempuri.org/", "FindMe", ApplicationUtility.SERVICE_URL, "customerId", "friendId", ApplicationUtility.USER_ID, element);
									}
								}catch (Exception e) {
									e.printStackTrace();
								}
								sendSoSHandler.sendEmptyMessage(0);
							};
						}.start();
					}else{
						Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
					}	
				}
			}
		});

		notificationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent notificationIntent=new Intent(context, NotificationsActivity.class);
				startActivityForResult(notificationIntent, 104);
			}
		});


		settingsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent settingsIntent=new Intent(context, SettingsActivity.class);
				startActivityForResult(settingsIntent, 105);
			}
		});

		requestButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent requestIntent=new Intent(context, RequestsActivity.class);
				startActivityForResult(requestIntent, 106);
			}
		});



		//		new Thread(){
		//			@Override
		//			public void run() {
		//				currentLocationNameServiceConnection = new WebServiceConnection("GET_CURRENT_LOCATION_NAME", "http://tempuri.org/IiEchoMobileService/GetCurrentLocation", "http://tempuri.org/", "GetCurrentLocation", ApplicationUtility.SERVICE_URL,"customerID",userId);
		//				locationNamesServiceConnection = new WebServiceConnection("GET_ALL_LOCATIONS_NAMES", "http://tempuri.org/IiEchoMobileService/GetGeoLocation", "http://tempuri.org/", "GetGeoLocation", ApplicationUtility.SERVICE_URL,"customerID","range",userId,"20");
		//				locationNamesHandler.sendEmptyMessage(0);
		//			}
		//		}.start();
		//		
		//		// Handler for getting the names of all the locations form the response
		//		
		//		locationNamesHandler = new Handler(){
		//			@Override
		//			public void handleMessage(Message msg) {
		//				try {
		//					System.out.println("===>>>  locationNamesServiceConnection object : "+locationNamesServiceConnection);
		//					if (locationNamesServiceConnection!=null) {
		////						getCurrentLocationName();
		////						getLocationsNames();
		//					}
		//				} catch (Exception e) {
		//					e.printStackTrace();
		//				}
		//			}
		//		};

		
		try {
			allUsersId = connectionManager.getAllUsersId();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {
			if (allUsersId != null) {
				if (allUsersId.length > 0) {
					new GetAllUserLocations(this, allUsersId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			if (allUsersId != null) {
				if (allUsersId.length > 0) {
					new GetAllUsersCurrentState(this, allUsersId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		
	}
	


	private void showGPSDisabledAlertToUser(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage("The app requires location services to be enabled. Services are disabled in your device. Would you like to enable it?")
		.setCancelable(false)
		.setPositiveButton("Enable Services",
				new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				Intent callGPSSettingIntent = new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(callGPSSettingIntent);
			}
		});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				dialog.cancel();
			}
		});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

	/**
	 * This function will check for value of counter to be 120 (2 minutes)
	 */
	private void  counterJob(){
		if(counter==0){
			new Thread(){
				@Override
				public void run() {
					while (counter < 121) {
						try {
							counter++;
							System.out.println("===>>>  Value of counter >>>>>>>> "+counter);
							if(counter==120){
								sendSMSFromSIMHandler.sendEmptyMessage(0);
							}
							Thread.sleep(1000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}				
				};
			}.start();			
		}
	}
	/**
	 * Function checks for that whether DB for the login user exists or not
	 * 
	 */
	public boolean doesDBExists(){
		File file=new File("/data/data/com.iecho.main/databases/" + ApplicationUtility.DB_NAME);
		return file.exists();
	}

	private void insertContacts(){
		try {
			responseJsonObject=serviceConnection.jsonResponse();

			if (responseJsonObject!=null) {

				JSONArray aryJSONStrings = new JSONArray(responseJsonObject.getString("ContactList"));

				for (int i = 0; i < aryJSONStrings.length(); i++) {
					JSONObject jsonObject = aryJSONStrings.getJSONObject(i);
					try{
						Log.i("ImageURL >>>>> ", jsonObject.getString("ImageURL"));
						image_url = jsonObject.getString("ImageURL");
						if(!image_url.equals("")){
						fetchUserImage(image_url);
						}
						ContactVO contactVO = new ContactVO(jsonObject.getString("FirstName"), jsonObject.getString("LastName"),
								 						jsonObject.getString("MobileNumber"),"" , "", "My Group", "1","1",jsonObject.getString("FriendId"),friendImageByteArray);
						Long row_id=connectionManager.addNewContact(contactVO);
						Log.v("===>>>  contact added >>>>>>> ",contactVO.toString());
						Log.v("===>>>  contact added at >>>>>>> ",row_id.toString());
						friendImageByteArray = null;
				}catch (Exception e) {
					// TODO: handle exception
				}
			}

		}} catch (Exception e) {

		}

	}
	private void updateContacts(){
		try {
			responseJsonObject=serviceConnection.jsonResponse();

			if (responseJsonObject!=null) {

				JSONArray aryJSONStrings = new JSONArray(responseJsonObject.getString("ContactList"));
				List<ContactVO> allExistingContacts = connectionManager.getAllContacts();

				for (int i = 0; i < aryJSONStrings.length(); i++) {
					JSONObject jsonObject = aryJSONStrings.getJSONObject(i);
					try{
						ContactVO contactVO = new ContactVO(jsonObject.getString("MobileNumber"), "My Group");
						if(allExistingContacts.contains(contactVO))
						{
							Log.v(contactVO.toString(), "Contact already Exist");
						}
						else{
						Log.v("ManageFriendsId >>>>> ", jsonObject.getString("FriendId"));
						contactVO.setUserId(jsonObject.getString("FriendId"));
						Log.v("FirstName >>>>> ", jsonObject.getString("FirstName"));
						contactVO.setFirstName(jsonObject.getString("FirstName"));
						Log.v("LastName >>>>> ", jsonObject.getString("LastName"));
						contactVO.setLastName(jsonObject.getString("LastName"));
						Log.v("ImageURL >>>>> ", jsonObject.getString("ImageURL"));
						image_url = jsonObject.getString("ImageURL");
						if(!image_url.equals("")){
							fetchUserImage(image_url);
						}
						contactVO.setImage(friendImageByteArray);
						contactVO.setIsFriend("1");
						contactVO.setIsWebContact("1");
						long row_id=connectionManager.addNewContact(contactVO);
						System.out.println("===>>>  contact added at >>>>>>> "+row_id);
						friendImageByteArray = null;}
				}catch (Exception e) {
					// TODO: handle exception
				}
				}
				}}
			catch (Exception e) {

		}

	}
	//	public static String locationNameStatus="",locationMsg="",currentLocationName=""; 

	//	private void getCurrentLocationName(){
	//		currentLocationResponseHashMap=currentLocationNameServiceConnection.serviceResponce();
	//		if(currentLocationResponseHashMap!=null){
	//			locationNameStatus = currentLocationResponseHashMap.get("status");
	//			locationMsg = currentLocationResponseHashMap.get("statusMsg");
	//			currentLocationName = currentLocationResponseHashMap.get("location");
	//			System.out.println("===>>>  locationNameStatus : "+locationNameStatus);
	//			System.out.println("===>>>  locationMsg : "+locationMsg);
	//			System.out.println("===>>>  currentLocationName : "+currentLocationName);
	//		}
	//	}

	//	private void getLocationsNames(){
	//		locationResponseJsonObject = locationNamesServiceConnection.jsonResponse();
	//		System.out.println("===>>>  locationResponseJsonObject >>>>>>>>>>>>>>> "+locationResponseJsonObject);
	//		if (locationResponseJsonObject!=null) {
	//			JSONArray aryJSONStrings;
	//			try {
	//				aryJSONStrings = new JSONArray(locationResponseJsonObject.getString("GeoLocation"));
	//				locationNames = new String[aryJSONStrings.length()];
	//				startingLocationNames = new String[aryJSONStrings.length()+1];
	//				startingLocationNames[0] = currentLocationName;
	//				for (int i = 0; i < aryJSONStrings.length(); i++) {
	//					JSONObject jsonObject = aryJSONStrings.getJSONObject(i);
	//					try{
	//						Log.i("Location Name >>>>> ", jsonObject.getString("Name"));
	//						locationNames[i] = jsonObject.getString("Name");
	//						startingLocationNames[i+1] = jsonObject.getString("Name");
	//					} catch (Exception e) {
	//						e.printStackTrace();
	//					}
	//				}
	//				SharedPreferences sharedPreferences = getSharedPreferences("locationPref"+userId, MODE_PRIVATE);
	//				String endBuilred = new String();
	//				for (int i = 0; i < locationNames.length; i++) {
	//					endBuilred = endBuilred.concat(locationNames[i]).concat("||");
	//					System.out.println("===>>>  endbuilder >>>>>>>>>>>>>>>>> "+endBuilred);
	//				}
	//				String startBuilred = new String();
	//				for (int i = 0; i < startingLocationNames.length; i++) {
	//					startBuilred = startBuilred.concat(startingLocationNames[i]).concat("||");
	//				}
	//				Editor editor = sharedPreferences.edit();
	//				editor.clear();
	//				editor.putString("endList", endBuilred.toString());
	//				editor.putString("startList", startBuilred.toString());
	//				editor.commit();
	//			} catch (JSONException e) {
	//				e.printStackTrace();
	//			}
	//		}
	//	}

	private void updateFriendContacts(){
		try {
			responseJsonObject=serviceConnection.jsonResponse();
			System.out.println("===>>>  responseJsonObject>>>>>>>>>>> "+responseJsonObject);
			if (responseJsonObject!=null) {

				JSONArray aryJSONStrings = new JSONArray(responseJsonObject.getString("ContactList"));
				for (int i = 0; i < aryJSONStrings.length(); i++) {
					JSONObject jsonObject = aryJSONStrings.getJSONObject(i);
					try{
						Log.i("ImageURL >>>>> ", jsonObject.getString("ImageURL"));
						image_url = jsonObject.getString("ImageURL");
					} catch (Exception e) {

					}
					if(!image_url.equals("")){
						fetchUserImage(image_url);
					}
					ContactVO contactVO = new ContactVO(jsonObject.getString("FirstName"), jsonObject.getString("LastName"),
														jsonObject.getString("Phone"),jsonObject.getString("EmailId") ,
														"Public", "My Group","0","1",
														jsonObject.getString("UserId"),friendImageByteArray);
					Long row_id=connectionManager.addNewContact(contactVO);
					Log.v("===>>>  contact added at >>>>>>> ",row_id.toString());
					Log.v("===>>>  contact added >>>>>>> ",contactVO.toString());
					friendImageByteArray = null;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private byte[] friendImageByteArray;


	private void fetchUserImage(String imageURL){
		try {
			URL url = new URL(imageURL);
			HttpGet httpRequest = null;
			httpRequest = new HttpGet(url.toURI());
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(httpRequest);
			HttpEntity entity = response.getEntity();
			BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
			InputStream input = b_entity.getContent();
			Bitmap bitmap = BitmapFactory.decodeStream(input);
			//			imageView.setImageBitmap(bitmap);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			friendImageByteArray = stream.toByteArray();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkUpdateFriendContacts(){
		try {
			responseJsonObject=serviceConnection.jsonResponse();
			System.out.println("===>>>  responseJsonObject>>>>>>>>>>> "+responseJsonObject);
			if (responseJsonObject!=null) {

				JSONArray aryJSONStrings = new JSONArray(responseJsonObject.getString("ContactList"));
				usersId=new String[aryJSONStrings.length()];
				fName=new String[aryJSONStrings.length()];
				lName=new String[aryJSONStrings.length()];
				contactNumber=new String[aryJSONStrings.length()];
				emailId=new String[aryJSONStrings.length()];

				for (int i = 0; i < aryJSONStrings.length(); i++) {
					JSONObject jsonObject = aryJSONStrings.getJSONObject(i);
					try{
						Log.i("ManageFriendsId >>>>> ", jsonObject.getString("UserId"));
						user_id=jsonObject.getString("UserId");
						usersId[i]=user_id;
					} catch (Exception e) {

					}try{
						Log.i("FirstName >>>>> ", jsonObject.getString("FirstName"));
						f_name=jsonObject.getString("FirstName");
						fName[i]=f_name;
					} catch (Exception e) {

					}try{
						Log.i("LastName >>>>> ", jsonObject.getString("LastName"));
						l_name=jsonObject.getString("LastName");
						lName[i]=l_name;
					} catch (Exception e) {

					}try{
						Log.i("MobileNumber >>>>> ", jsonObject.getString("Phone"));
						number=jsonObject.getString("Phone");
						contactNumber[i]=number;
					} catch (Exception e) {

					}try{
						Log.i("EmailId >>>>> ", jsonObject.getString("EmailId"));
						email=jsonObject.getString("EmailId");
						emailId[i]=email;
					} catch (Exception e) {

					}try{
						Log.i("FriendStatus >>>>> ", jsonObject.getString("FriendStatus"));
						type=jsonObject.getString("FriendStatus");
					} catch (Exception e) {

					}try{
						Log.i("ImageURL >>>>> ", jsonObject.getString("ImageURL"));
						image_url = jsonObject.getString("ImageURL");
					} catch (Exception e) {

					}
					if(!image_url.equals("")){
						fetchUserImage(image_url);
					}
					try{
						imageArrayList.add(friendImageByteArray);
					}catch (Exception e) {
						e.printStackTrace();
					}
					friendImageByteArray = null;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}



	private void checkForContactDelete(){
		try{
			String[] dbUIDArray=connectionManager.getAllUsersId();
			boolean flag=false;
			for (String element : dbUIDArray) {
				for (String element2 : usersId) {
					if(element.equals(element2)){
						flag=true;
					}
				}
				System.out.println("===>>>  ====>>>>>>>>>"+flag);
				if(flag==false){
					toDeleteUID=element;
					System.out.println(element);
					long row_id=connectionManager.deleteContact(Long.parseLong(toDeleteUID));
					System.out.println("===>>>  contact deleted at>>>>>> "+row_id);
				}
				flag=false;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpViews() {
		contactsButton = (Button) findViewById(R.id.home_bottom_contacts_btn);
		settingsButton = (Button) findViewById(R.id.home_bottom_settings_btn);
		requestButton = (Button) findViewById(R.id.home_bottom_requests_btn);
		notificationButton = (Button) findViewById(R.id.home_notification_btn);
		friendsButton= (Button) findViewById(R.id.home_friends_btn);
		sosButton= (Button) findViewById(R.id.home_sos_btn);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==202) {
			Intent loginIntent=new Intent(context, LoginActivity.class);
			finish();
			startActivityForResult(loginIntent, 100);
		}else if (requestCode == 1285) {
			try{
				sosBitmapImage = (Bitmap) data.getExtras().get("data"); 
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				sosBitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				sosByteArray = stream.toByteArray();
			}catch (Exception e) {
				System.out.println("===>>>  camera exception >>>>>>>>>>>>>>>>>>>>");
				e.printStackTrace();
			}finally{
				if(counter==0 || counter==1000){
					if (counter==1000) {
						counter=0;
					}
					counterJob();
				}
				if(new ApplicationUtility(context).checkNetworkConnection()){
					Toast.makeText(context, "State changed to Public", Toast.LENGTH_LONG).show();
					progressDialog=ProgressDialog.show(context, "", "please wait...");
					new Thread(){
						@Override
						public void run() {
							serviceConnection=new WebServiceConnection("CHANGE_USER_STATE", "http://tempuri.org/IiEchoMobileService/SetCustomerState", "http://tempuri.org/", "SetCustomerState", ApplicationUtility.SERVICE_URL, "customerId", "state", "hour", ApplicationUtility.USER_ID, "1", "0");
							serviceConnection=new WebServiceConnection("SEND_SOS", "http://tempuri.org/IiEchoMobileService/SOSRequest", "http://tempuri.org/", "SOSRequest", ApplicationUtility.SERVICE_URL, "customerId", "imageByte","CountryCode",  ApplicationUtility.USER_ID, sosByteArray,"1");
							final String[] userIDS=new DataBaseConnectionManager(context).getAllUsersId();
							try{
								for (String element : userIDS) {
									System.out.println("===>>>  Sending request to :"+element);
									new WebServiceConnection("FIND_ME_REQUEST","http://tempuri.org/IiEchoMobileService/FindMe", "http://tempuri.org/", "FindMe", ApplicationUtility.SERVICE_URL, "customerId", "friendId", ApplicationUtility.USER_ID, element);
								}
							}catch (Exception e) {
								e.printStackTrace();
							}
							sendSoSHandler.sendEmptyMessage(0);
						};
					}.start();	
				}else{
					Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
				}
			}
		} 
	}




	@Override
	protected void onPause() {
		super.onPause();
		connectionManager.closeDB();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(locationServiceIntent!=null){
			stopService(locationServiceIntent);
		}
		if(locationArrayIntent!=null){
			stopService(locationArrayIntent);
		}
	}

	private void sendSMSFromSim(String number,String message){
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(number, null, message, null, null);
	}

	private Intent locationServiceIntent, locationArrayIntent;

	@Override
	protected void onResume() {
		super.onResume();
		/*locationServiceIntent=new Intent(context,SendUpdatedLocationService.class);
		startService(locationServiceIntent);*/
	}

	/*@Override
	public void onAttachedToWindow()
	{  
		Log.i("WELCOME >>>", "onAttachedToWindow");
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
		super.onAttachedToWindow();  
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			Log.i("GOING TO HOME", "BOTAO HOME");
			stopService(locationServiceIntent);
			Intent startMain = new Intent(Intent.ACTION_MAIN);
			startMain.addCategory(Intent.CATEGORY_HOME);
			startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startMain);

			return true;
		}
		return super.onKeyDown(keyCode, event);    
	}*/

}
