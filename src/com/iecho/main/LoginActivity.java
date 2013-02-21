package com.iecho.main;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.webservice.WebServiceConnection;

public class LoginActivity extends Activity {
	private EditText emailEditText,passwordEditText;
	private CheckBox rememberCheckBox;
	private Button loginUserButton,registerUserButton,forgotPasswordButton;
	private Context context;
	private SharedPreferences loginSharedPreferences;
	private ProgressDialog progressDialog;
	private Handler loginHandler;
	private WebServiceConnection serviceConnection;
	private HashMap<String, String> responseHashMap;
	private String status,statusMsg,userId,isRegisteredOnWeb;
	private boolean rememberCheck=false;
	private String regUserId,regPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		context=this;
		setUpViews();
		
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			System.out.println("services are enabled");
		}else{
			showGPSDisabledAlertToUser();
		}

		Button infoButton=(Button)findViewById(R.id.login_info);
		infoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent infoIntent=new Intent(context, AboutIechoActivity.class);
				startActivity(infoIntent);
			}
		});

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
						if(rememberCheck){
							loginSharedPreferences=getSharedPreferences("LOGIN_DETAILS", MODE_PRIVATE);
							Editor editor=loginSharedPreferences.edit();
							editor.putString("email", emailEditText.getText().toString());
							editor.putString("password", passwordEditText.getText().toString());
							System.out.println("email >>>>>>>>>>>>> "+emailEditText.getText().toString());
							System.out.println("password >>>>>>>>>>>> "+passwordEditText.getText().toString());
							editor.commit();
						}

						ApplicationUtility.DB_NAME=emailEditText.getText().toString();
						ApplicationUtility.PASSWORD=passwordEditText.getText().toString();
						Intent homeIntent=new Intent(context,HomeActivity.class);
						homeIntent.putExtra("isRegistered", isRegisteredOnWeb);
						homeIntent.putExtra("userId", userId);
						finish();
						startActivityForResult(homeIntent, 100);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(context, "Server error", Toast.LENGTH_LONG).show();
				}

			}
		};

		loginUserButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(new ApplicationUtility(context).checkNetworkConnection()){		
					final String email=emailEditText.getText().toString();
					final String password=passwordEditText.getText().toString();
					if(email.equals("")||password.equals("")){
						new ApplicationUtility(context,LoginActivity.this).errorDialogAlertBox("Please fill all the fields.", "Login Error!", R.drawable.icon);
					}else{
						progressDialog=ProgressDialog.show(context, "", "please wait...");
						rememberCheck=rememberCheckBox.isChecked();
						System.out.println(">>>>>>>>>>>>>>>> "+rememberCheck);

						new Thread(){
							@Override
							public void run() {
								serviceConnection=new WebServiceConnection("LOGIN_USER","http://tempuri.org/IiEchoMobileService/AuthenticateUser", "http://tempuri.org/", "AuthenticateUser", ApplicationUtility.SERVICE_URL, "email", "password", email, password);							
								loginHandler.sendEmptyMessage(0);
							}
						}.start();
					}
				}else{
					Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
				}
			}
		});

		registerUserButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent registerIntent=new Intent(context,RegisterUserActivity.class);
				startActivityForResult(registerIntent, 101);
			}
		});

		forgotPasswordButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent forgotPasswordIntent=new Intent(context,ForgotPasswordActivity.class);
				startActivityForResult(forgotPasswordIntent, 102);
			}
		});

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

	private void setUpViews(){
		emailEditText=(EditText)findViewById(R.id.login_email_edit);
		passwordEditText=(EditText)findViewById(R.id.login_password_edit);
		rememberCheckBox=(CheckBox)findViewById(R.id.login_remember_check);
		loginUserButton=(Button)findViewById(R.id.login_login_btn);
		registerUserButton=(Button)findViewById(R.id.login_register_btn);
		forgotPasswordButton=(Button)findViewById(R.id.login_forgot_password_btn);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode==1254) {
			try {
				regUserId=data.getExtras().getString("emailid");
				regPassword=data.getExtras().getString("password");
				emailEditText.setText(regUserId);
				passwordEditText.setText(regPassword);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
