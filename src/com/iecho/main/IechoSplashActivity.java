package com.iecho.main;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.webservice.WebServiceConnection;

public class IechoSplashActivity extends Activity {
	private MyTimerTask timerTask;
	private Context context;
	private boolean backFlag = true;
	private ApplicationUtility utility;
	private SharedPreferences loginSharedPreferences;
	private String emailId,password;
	private Handler loginHandler;
	private WebServiceConnection serviceConnection;
	private HashMap<String, String> responseHashMap;
	private String status,statusMsg,userId,isRegisteredOnWeb;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.main);
		loginSharedPreferences=getSharedPreferences("LOGIN_DETAILS", MODE_PRIVATE);
		emailId=loginSharedPreferences.getString("email", "");
		password=loginSharedPreferences.getString("password", "");
		System.out.println("email is is : "+emailId);
		System.out.println("password is : "+password);


		utility=new ApplicationUtility(context,this);
		loginHandler=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				try {
					if (progressDialog!=null) {
						progressDialog.dismiss();
					}
					responseHashMap=serviceConnection.serviceResponce();
					if(responseHashMap!=null){
						status=responseHashMap.get("status");
						statusMsg=responseHashMap.get("statusMsg");
						userId=responseHashMap.get("userId");
						isRegisteredOnWeb=responseHashMap.get("isRegisteredOnWeb");
					}
					if(status.equals("0")){
						Toast.makeText(context, "Invalid user details", 3000).show();
					}else{

						ApplicationUtility.DB_NAME=emailId;
						ApplicationUtility.PASSWORD=password;
						Intent homeIntent=new Intent(context,HomeActivity.class);
						homeIntent.putExtra("isRegistered", isRegisteredOnWeb);
						homeIntent.putExtra("userId", userId);
						finish();
						startActivityForResult(homeIntent, 100);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		if(!emailId.equals("")||!password.equals("")){
			progressDialog=ProgressDialog.show(context, "", "please wait...");
		}
		boolean connectivityAvailable=utility.checkNetworkConnection();

		if (connectivityAvailable) {
			try {
				timerTask = new MyTimerTask();
				Timer timer = new Timer();
				timer.schedule(timerTask, 5000);
			} catch (Exception e) {
				System.out.println("IechoSplashActivity>>>>>>onCreate>>>>> exception>>>> "+ e);
			}
		} else {
			utility.dialogAlertBox();
		}
	}



	private class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			try {
				Intent loginIntent =null;
				if(emailId.equals("")||password.equals("")){

					loginIntent = new Intent(context, LoginActivity.class);
					finish();
					if (backFlag) {
						startActivity(loginIntent);
					}
				}else{
					new Thread(){
						@Override
						public void run() {
							ApplicationUtility.PASSWORD=password;
							serviceConnection=new WebServiceConnection("LOGIN_USER","http://tempuri.org/IiEchoMobileService/AuthenticateUser", "http://tempuri.org/", "AuthenticateUser", ApplicationUtility.SERVICE_URL, "email", "password", emailId, password);							
							loginHandler.sendEmptyMessage(0);
						}
					}.start();
				}

			} catch (Exception e) {
				System.out.println("MyTimerTask>>>>>>run>>>>> exception>>>> "+ e);
			}
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		System.out.println("back pressed >>>>>>> " + backFlag);
		backFlag = false;
		System.out.println("back pressed >>> changed value >>>> " + backFlag);
	}
}
