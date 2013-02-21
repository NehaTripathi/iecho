package com.iecho.main;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.services.IechoLocationManagerService;
import com.iecho.services.LocationListUpdateService;
import com.iecho.webservice.WebServiceConnection;

public class SettingsActivity extends Activity {
	private Button editProfileButton,changePasswordButton,switchModeButton,logoutButton,homeButton,locationUpdateRefreshButton;
	private Context context;
	private TextView refreshTimetTextView;
	private SharedPreferences refreshTimeSharedPreferences;
	private SharedPreferences loginSharedPreferences;
	private ProgressDialog progressDialog;
	private Handler handler,checkSubscriptionHandler,sosImageHandler,checkSubscriptionHandler_2;
	private WebServiceConnection serviceConnection,checkSubscriptionServiceConnection;
	private HashMap<String, String> hashMap,checkSubscriptionHashMap;
	private byte[] sosByteArray;
	private Bitmap sosBitmapImage;
	private HashMap<String, String> sosResponseHashMap;
	private String stateID;
	public static String PAYMENT_PROCESS="";
	public static long locationRefreshTime = 600000;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		context=this;
		PAYMENT_PROCESS="";
		setUpViews();
		Button infoButton=(Button)findViewById(R.id.settings_info);
		infoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent infoIntent=new Intent(context, AboutIechoActivity.class);
				startActivity(infoIntent);
			}
		});

		handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(progressDialog!=null){
					progressDialog.dismiss();
				}

				int value=msg.what;
				hashMap=serviceConnection.serviceResponce();
				if(hashMap!=null){
					String status=hashMap.get("status");
					if(status.equals("true")){
						if(value==0){
							stateID="0";
							ApplicationUtility.USER_CURRENT_STATE="Private";
							Toast.makeText(context,"State changed to Private",3000).show();
						}else if(value==1){
							stateID="2";
							ApplicationUtility.USER_CURRENT_STATE="Public";
							Toast.makeText(context,"State changed to Public",3000).show();
						}else if(value==2){
							stateID="1";
							ApplicationUtility.USER_CURRENT_STATE="Secret";
							Toast.makeText(context,"State changed to Secret",3000).show();
						}
						/**
						 * Thread for re-setting of the requests as per the state.
						 */
						new Thread(){
							@Override
							public void run() {
								try{
									serviceConnection=new WebServiceConnection("RESET_REQUESTS","http://tempuri.org/IiEchoMobileService/UpdateIncommingFriendRequest", "http://tempuri.org/", "UpdateIncommingFriendRequest", ApplicationUtility.SERVICE_URL, "customerId", "customerState", ApplicationUtility.USER_ID,stateID );
								}catch (Exception e) {
									e.printStackTrace();
								}
							}
						}.start();
					}else{
						Toast.makeText(context,"Server error.State not changed",3000).show();
					}
				}
			}
		};

		/**
		 * Handler for checking the subscription response
		 */
		checkSubscriptionHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if (progressDialog!=null) {
					progressDialog.dismiss();
				}
				try{
					hashMap=serviceConnection.serviceResponce();
					if(hashMap!=null){
						String status=hashMap.get("status");
						String message=hashMap.get("statusMsg");
						if(status.equals("true") && message.equals("Subscribe customer.")){
							ApplicationUtility.IS_SUBSCRIBED_USER="YES";
							Toast.makeText(context, "Subscribed Successfully", Toast.LENGTH_LONG).show();
							/**
							 * Open dialog for upload sos image as user is successfully subscribed
							 */
							sosPictureUploadDialog();
						}else{
							ApplicationUtility.IS_SUBSCRIBED_USER="NO";
							Toast.makeText(context, "User Not Subscribed", Toast.LENGTH_LONG).show();
						}
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		/**
		 * handler for sos image response
		 */
		sosImageHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if (progressDialog!=null) {
					progressDialog.dismiss();
				}
				try {
					sosResponseHashMap=serviceConnection.serviceResponce();
					if (sosResponseHashMap!=null) {
						String status=sosResponseHashMap.get("status");
						if (status.equals("true")) {
							Toast.makeText(context, "SOS image saved successfully", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(context, "Image not saved.Server error", Toast.LENGTH_LONG).show();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		editProfileButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(new ApplicationUtility(context).checkNetworkConnection()){
					Intent editProfileIntent=new Intent(context, EditProfileActivity.class);
					startActivityForResult(editProfileIntent, 107);
				}else{
					Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
				}
			}
		});

		changePasswordButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent changePasswordIntent=new Intent(context, ChangePasswordActivity.class);
				startActivityForResult(changePasswordIntent, 108);
			}
		});


		switchModeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast.makeText(context, "Current state is "+ApplicationUtility.USER_CURRENT_STATE, Toast.LENGTH_LONG).show();
				final Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.custom_dialog);
				dialog.setTitle("Set Status As                       ");
				dialog.getWindow().setTitleColor(Color.parseColor("#008bd0"));
				dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
				Button button1 = (Button) dialog.findViewById(R.id.dialog_btn1);
				button1.setText("Private");
				Button button2= (Button) dialog.findViewById(R.id.dialog_btn2);
				button2.setText("Secret");
				Button button3= (Button) dialog.findViewById(R.id.dialog_btn3);
				button3.setText("Public");

				button1.setVisibility(View.VISIBLE);
				button2.setVisibility(View.VISIBLE);
				button3.setVisibility(View.VISIBLE);

				button1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						if (ApplicationUtility.IS_SUBSCRIBED_USER.equals("YES")) {
							/**
							 * Switch user state to private
							 */
							if(new ApplicationUtility(context).checkNetworkConnection()){
								progressDialog=ProgressDialog.show(context, "", "please wait...");
								new Thread(){
									@Override
									public void run() {
										serviceConnection=new WebServiceConnection("CHANGE_USER_STATE", "http://tempuri.org/IiEchoMobileService/SetCustomerState", "http://tempuri.org/", "SetCustomerState", ApplicationUtility.SERVICE_URL, "customerId", "state", "hour", ApplicationUtility.USER_ID, "0", "0");

										handler.sendEmptyMessage(0);
									}
								}.start();
							}else{
								Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
							}
						} else {
							buySubscriptionDialog();
						}
					}
				});
				button2.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						/**
						 * Switch user state to secret
						 */
						dialog.dismiss();
						if (ApplicationUtility.IS_SUBSCRIBED_USER.equals("YES")) {
							switchUserStateToSecret();					
						} else {
							buySubscriptionDialog();
						}
					}
				});
				button3.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						/*
						 * Switch user state to public
						 */
						if(new ApplicationUtility(context).checkNetworkConnection()){
							progressDialog=ProgressDialog.show(context, "", "please wait...");
							new Thread(){
								@Override
								public void run() {
									serviceConnection=new WebServiceConnection("CHANGE_USER_STATE", "http://tempuri.org/IiEchoMobileService/SetCustomerState", "http://tempuri.org/", "SetCustomerState", ApplicationUtility.SERVICE_URL, "customerId", "state", "hour", ApplicationUtility.USER_ID, "2", "0");
									handler.sendEmptyMessage(1);
								}
							}.start();
						}else{
							Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
						}
					}
				});
				dialog.show();
			}
		});
		
		locationUpdateRefreshButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(ApplicationUtility.IS_SUBSCRIBED_USER.equals("YES")){
					try{
						refreshGPSDialog();
					}catch (Exception e) {
						e.printStackTrace();
					}
				}else {
					Toast.makeText(context, "Subscribe app to unlock this feature", Toast.LENGTH_LONG).show();
				}
			}
		});



		logoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/**
				 * Thread for reseting the previously accepted find requests
				 */
				new Thread(){
					@Override
					public void run() {
						serviceConnection=new WebServiceConnection("RESET_FOLLOW_YOU_ACCEPTED_REQUESTS", "http://tempuri.org/IiEchoMobileService/ResetFollowYouAcceptedRequest", "http://tempuri.org/", "ResetFollowYouAcceptedRequest", ApplicationUtility.SERVICE_URL,"customerId",ApplicationUtility.USER_ID);	
						serviceConnection=new WebServiceConnection("RESET_FIND_ME_ACCEPTED_REQUESTS", "http://tempuri.org/IiEchoMobileService/ResetFindMeAcceptedRequest", "http://tempuri.org/", "ResetFindMeAcceptedRequest", ApplicationUtility.SERVICE_URL,"customerId",ApplicationUtility.USER_ID);
					};
				}.start();

				/**
				 * Stopping service
				 */
//				locationServiceIntent=new Intent(context,SendUpdatedLocationService.class);
//				startService(locationServiceIntent);
				
				locationServiceIntent=new Intent(context,IechoLocationManagerService.class);
				startService(locationServiceIntent);

				loginSharedPreferences=getSharedPreferences("LOGIN_DETAILS", MODE_PRIVATE);
				Editor editor=loginSharedPreferences.edit();
				editor.clear();
				editor.commit();
				System.out.println("Logout >>>>>>>>>>>>>>>>>>");
				setResult(202);
				finish();
			}
		});
		try{
			refreshTimeSharedPreferences = getSharedPreferences("location_refresh_rate"+ApplicationUtility.USER_ID, MODE_PRIVATE);
			locationRefreshTime = refreshTimeSharedPreferences.getLong("time", 600000);
			if (locationRefreshTime == 300000) {
				refreshTimetTextView.setText("5 mins");
			} else if (locationRefreshTime == 600000){
				refreshTimetTextView.setText("10 mins");
			}else if (locationRefreshTime == 900000){
				refreshTimetTextView.setText("15 mins");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	

		homeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});



	}




	private void switchUserStateToSecret(){

		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.custom_dialog);
		dialog.setTitle("Switch to Secret              ");
		dialog.getWindow().setTitleColor(Color.parseColor("#008bd0"));
		dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
		Button button1 = (Button) dialog.findViewById(R.id.dialog_btn1);
		button1.setText("For 6 hour");
		Button button2= (Button) dialog.findViewById(R.id.dialog_btn2);
		button2.setText("For 12 hour");
		Button button3= (Button) dialog.findViewById(R.id.dialog_btn3);
		button3.setText("For 24 hour");
		Button button4= (Button) dialog.findViewById(R.id.dialog_btn4);
		button4.setText("Cancel");

		button1.setVisibility(View.VISIBLE);
		button2.setVisibility(View.VISIBLE);
		button3.setVisibility(View.VISIBLE);
		button4.setVisibility(View.VISIBLE);
		/**
		 * switch request
		 */
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(new ApplicationUtility(context).checkNetworkConnection()){
					dialog.dismiss();
					progressDialog=ProgressDialog.show(context, "", "please wait...");
					new Thread(){
						@Override
						public void run() {
							serviceConnection=new WebServiceConnection("CHANGE_USER_STATE", "http://tempuri.org/IiEchoMobileService/SetCustomerState", "http://tempuri.org/", "SetCustomerState", ApplicationUtility.SERVICE_URL, "customerId", "state", "hour", ApplicationUtility.USER_ID, "1", "6");
							handler.sendEmptyMessage(2);
						}
					}.start();
				}else{
					Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
				}
			}
		});
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(new ApplicationUtility(context).checkNetworkConnection()){
					dialog.dismiss();
					progressDialog=ProgressDialog.show(context, "", "please wait...");
					new Thread(){
						@Override
						public void run() {
							serviceConnection=new WebServiceConnection("CHANGE_USER_STATE", "http://tempuri.org/IiEchoMobileService/SetCustomerState", "http://tempuri.org/", "SetCustomerState", ApplicationUtility.SERVICE_URL, "customerId", "state", "hour", ApplicationUtility.USER_ID, "1", "12");
							handler.sendEmptyMessage(2);
						}
					}.start();
				}else{
					Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
				}
			}
		});
		button3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(new ApplicationUtility(context).checkNetworkConnection()){
					dialog.dismiss();
					progressDialog=ProgressDialog.show(context, "", "please wait...");
					new Thread(){
						@Override
						public void run() {
							serviceConnection=new WebServiceConnection("CHANGE_USER_STATE", "http://tempuri.org/IiEchoMobileService/SetCustomerState", "http://tempuri.org/", "SetCustomerState", ApplicationUtility.SERVICE_URL, "customerId", "state", "hour", ApplicationUtility.USER_ID, "1", "24");
							//							updateRequests
							handler.sendEmptyMessage(2);
						}
					}.start();
				}else{
					Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
				}
			}
		});
		button4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private void setUpViews(){
		locationUpdateRefreshButton = (Button) findViewById(R.id.edit_profile_choose_frequency_btn);
		refreshTimetTextView = (TextView) findViewById(R.id.edit_profile_sos_update_text_view);
		editProfileButton=(Button)findViewById(R.id.settings_edit_profile_btn);
		changePasswordButton=(Button)findViewById(R.id.settings_change_password_btn);
		switchModeButton=(Button)findViewById(R.id.settings_switch_mode_btn);
		logoutButton=(Button)findViewById(R.id.settings_logout_btn);
		homeButton = (Button) findViewById(R.id.settings_home_btn);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode==201) {
			finish();
		}else if (requestCode == 159) {
			try{
				sosBitmapImage = (Bitmap) data.getExtras().get("data"); 
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				sosBitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				sosByteArray = stream.toByteArray();
				System.out.println("sos image byte array >>>>>>>>>>>>>>> "+sosByteArray);
			}catch (Exception e) {
				System.out.println("camera exception >>>>>>>>>>>>>>>>>>>>");
				e.printStackTrace();
			}finally{
				if(new ApplicationUtility(context).checkNetworkConnection()){
					if (sosByteArray!=null) {
						progressDialog=ProgressDialog.show(context, "", "please wait...");
						/**
						 * Thread for uploading the SOS image after the successful registration
						 */
						new Thread(){
							@Override
							public void run() {
								serviceConnection=new WebServiceConnection("UPLOAD_SOS_IMAGE", "http://tempuri.org/IiEchoMobileService/SaveSOSRequestImage", "http://tempuri.org/", "SaveSOSRequestImage", ApplicationUtility.SERVICE_URL, "customerId", "userImage",  ApplicationUtility.USER_ID, sosByteArray);		
								sosImageHandler.sendEmptyMessage(0);
							};
						}.start();	
					} else {
						Toast.makeText(context, "No image to upload", Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private Intent locationServiceIntent;

	@Override
	protected void onResume() {
		super.onResume();
		/*locationServiceIntent=new Intent(context,SendUpdatedLocationService.class);
		startService(locationServiceIntent);*/

		/**
		 * Thread for checking whether the user is subscribed or not
		 */
		new Thread(){
			@Override
			public void run() {
				checkSubscriptionServiceConnection=new WebServiceConnection("CHECK_USER_SUBSCRIPTION", "http://tempuri.org/IiEchoMobileService/IsSubscribeCustomer", "http://tempuri.org/", "IsSubscribeCustomer", ApplicationUtility.SERVICE_URL,"CustomerId",ApplicationUtility.USER_ID);
				checkSubscriptionHandler_2.sendEmptyMessage(0);
			};
		}.start();

		/**
		 * Handler for updating the user subscription status
		 */
		checkSubscriptionHandler_2=new Handler(){
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
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		System.out.println(">>>>>>>>>>>>>>>>>back form web browser<<<<<<<<<<<<<<<<<<<<");
		if (PAYMENT_PROCESS.equals("Payment_Begin")) {
			PAYMENT_PROCESS="";
			/**
			 * Thread for checking whether the user is subscribed or not
			 */
			if(new ApplicationUtility(context).checkNetworkConnection()){
				try {
					progressDialog=ProgressDialog.show(context, "", "updating status...");
				} catch (Exception e) {
					e.printStackTrace();
				}
				new Thread(){
					@Override
					public void run() {
						serviceConnection=new WebServiceConnection("CHECK_USER_SUBSCRIPTION", "http://tempuri.org/IiEchoMobileService/IsSubscribeCustomer", "http://tempuri.org/", "IsSubscribeCustomer", ApplicationUtility.SERVICE_URL,"CustomerId",ApplicationUtility.USER_ID);
						checkSubscriptionHandler.sendEmptyMessage(0);
					};
				}.start();
			}else{
				Toast.makeText(context, "Status not updated.Please check network connection", Toast.LENGTH_LONG).show();
			}
		}
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

	private void sosPictureUploadDialog(){

		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.custom_dialog);
		dialog.setTitle("Upload SOS Image              ");
		dialog.getWindow().setTitleColor(Color.parseColor("#008bd0"));
		dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
		Button button1 = (Button) dialog.findViewById(R.id.dialog_btn1);
		button1.setText("Capture Picture & Upload");
		Button button2= (Button) dialog.findViewById(R.id.dialog_btn2);
		button2.setText("Cancel");

		button1.setVisibility(View.VISIBLE);
		button2.setVisibility(View.VISIBLE);

		/**
		 * switch request
		 */
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				// this will open camera
				Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, 159);
			}
		});
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	private void buySubscriptionDialog(){
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.custom_dialog);
		dialog.setTitle("Subscribe to iecho             ");
		dialog.getWindow().setTitleColor(Color.parseColor("#008bd0"));
		dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
		Button button1 = (Button) dialog.findViewById(R.id.dialog_btn1);
		button1.setText("Subscribe");
		Button button2= (Button) dialog.findViewById(R.id.dialog_btn2);
		button2.setText("Cancel");

		button1.setVisibility(View.VISIBLE);
		button2.setVisibility(View.VISIBLE);

		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(new ApplicationUtility(context).checkNetworkConnection()){
					if(new ApplicationUtility(context).checkNetworkConnection()){
						PAYMENT_PROCESS="Payment_Begin";
						Intent subscriptionIntent=new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.iechomobility.com/mobilepayment.aspx?email="+ApplicationUtility.DB_NAME+"&password="+ApplicationUtility.PASSWORD));
						System.out.println("Loading URL >>>>>>>>>>>"+"http://www.iechomobility.com/mobilepayment.aspx?email="+ApplicationUtility.DB_NAME+"&password="+ApplicationUtility.PASSWORD);
						startActivityForResult(subscriptionIntent,2048);
					}else{
						Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
				}
			}
		});
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	private void refreshGPSDialog() {
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.custom_dialog);
		dialog.setTitle("Set GPS refresh frequency        ");
		dialog.getWindow().setTitleColor(Color.parseColor("#008bd0"));
		dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
		Button button1 = (Button) dialog.findViewById(R.id.dialog_btn1);
		button1.setText("5 minutes");
		Button button2= (Button) dialog.findViewById(R.id.dialog_btn2);
		button2.setText("10 minutes");
		Button button3= (Button) dialog.findViewById(R.id.dialog_btn3);
		button3.setText("15 minutes");

		button1.setVisibility(View.VISIBLE);
		button2.setVisibility(View.VISIBLE);
		button3.setVisibility(View.VISIBLE);

		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Editor edit = refreshTimeSharedPreferences.edit();
				edit.putLong("time", 300000);
				edit.commit();
				refreshTimetTextView.setText("5 mins");
				LocationListUpdateService.locationRefreshTime = 300000;
				Toast.makeText(context, "GPS refresh frequency set to 5 minutes", Toast.LENGTH_LONG).show();
			}
		});
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Editor edit = refreshTimeSharedPreferences.edit();
				edit.putLong("time", 600000);
				edit.commit();
				refreshTimetTextView.setText("10 mins");
				LocationListUpdateService.locationRefreshTime = 600000;
				Toast.makeText(context, "GPS refresh frequency set to 10 minutes", Toast.LENGTH_LONG).show();
			}
		});
		button3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Editor edit = refreshTimeSharedPreferences.edit();
				edit.putLong("time", 900000);
				edit.commit();
				refreshTimetTextView.setText("15 mins");
				LocationListUpdateService.locationRefreshTime = 900000;
				Toast.makeText(context, "GPS refresh frequency set to 15 minutes", Toast.LENGTH_LONG).show();
			}
		});
		dialog.show();
	}
}