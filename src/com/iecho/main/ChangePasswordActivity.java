package com.iecho.main;

import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.services.SendUpdatedLocationService;
import com.iecho.webservice.WebServiceConnection;

public class ChangePasswordActivity extends Activity {
	private EditText oldPasswordEditText, newPasswordEditText,renewPasswordEditText;
	private Button changePasswordButton,homeButton;
	public Context context;
	private WebServiceConnection serviceConnection;
	private Handler changePasswordHandler;
	private ProgressDialog progressDialog;
	private HashMap<String, String> responseHashMap;
	private String status="",statusMessage="";
	private SharedPreferences sharedPreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_password);
		context=this;
		setUpViews();

		Button infoButton=(Button)findViewById(R.id.change_pass_info);
		infoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent infoIntent=new Intent(context, AboutIechoActivity.class);
				startActivity(infoIntent);
			}
		});

		changePasswordHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if (progressDialog!=null) {
					progressDialog.dismiss();
				}
				responseHashMap=serviceConnection.serviceResponce();
				if(responseHashMap!=null){
					status=responseHashMap.get("status");
					statusMessage=responseHashMap.get("statusMsg");
					System.out.println(statusMessage);
				}
				if(status.equals("true")){
					// updating sharedpref for changed password details
					sharedPreferences=getSharedPreferences("LOGIN_DETAILS", MODE_PRIVATE);
					String password=sharedPreferences.getString("password", "");
					if(password.equals("")){
						System.out.println("password is not in preference...");
					}else{
						System.out.println("updating preference...");
						Editor editor=sharedPreferences.edit();
						editor.putString("password", newPasswordEditText.getText().toString());
						editor.commit();	
					}
					// updating the password in app utils
					ApplicationUtility.PASSWORD=newPasswordEditText.getText().toString();
					Toast.makeText(context, "Password changed successfully", Toast.LENGTH_LONG).show();
					finish();
				}else if(status.equals("false")){
					Toast.makeText(context, "Password not changed", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(context, "Server error", Toast.LENGTH_LONG).show();
				}
			}
		};

		changePasswordButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String oldPassword = oldPasswordEditText.getText().toString();
				final String newPassword = newPasswordEditText.getText().toString();
				String renewPassword = renewPasswordEditText.getText().toString();
				if(oldPassword.equals("")||newPassword.equals("")||renewPassword.equals("")){
					new ApplicationUtility(context, ChangePasswordActivity.this).errorDialogAlertBox("Please enter all the fields", "Change Password Error!", R.drawable.icon);
				}else{
					if(newPassword.equals(renewPassword)){
						if(oldPassword.equals(ApplicationUtility.PASSWORD)){
							if(new ApplicationUtility(context).checkNetworkConnection()){
								progressDialog=ProgressDialog.show(context, "", "please wait...");
								new Thread(){
									@Override
									public void run() {
										try{
											serviceConnection=new WebServiceConnection("CHANGE_PASSWORD", "http://tempuri.org/IiEchoMobileService/ChangePassword", "http://tempuri.org/", "ChangePassword", ApplicationUtility.SERVICE_URL, "email", "oldPassword", "newPassword", ApplicationUtility.DB_NAME, oldPassword, newPassword);
											changePasswordHandler.sendEmptyMessage(0);
										}catch (Exception e) {
											e.printStackTrace();
										}
									}
								}.start();
							}else{
								Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
							}
						}else{
							new ApplicationUtility(context, ChangePasswordActivity.this).errorDialogAlertBox("Old password is wrong", "Change Password Error!", R.drawable.icon);
						}
					}else{
						new ApplicationUtility(context, ChangePasswordActivity.this).errorDialogAlertBox("Password and Re-enter Password not match", "Change Password Error!", R.drawable.icon);	
					}
				}
			}
		});
		homeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(201);
				finish();
			}
		});

	}

	
	private Intent locationServiceIntent;
	@Override
	protected void onResume() {
		super.onResume();
		/*locationServiceIntent=new Intent(context,SendUpdatedLocationService.class);
		startService(locationServiceIntent);*/
	}
	
	private void setUpViews() {
		oldPasswordEditText = (EditText) findViewById(R.id.change_password_old_password_edit);
		newPasswordEditText = (EditText) findViewById(R.id.change_password_new_password_edit);
		renewPasswordEditText = (EditText) findViewById(R.id.change_password_renew_password_edit);
		changePasswordButton = (Button) findViewById(R.id.change_password_submit_btn);
		homeButton= (Button) findViewById(R.id.change_password_home_btn);
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
