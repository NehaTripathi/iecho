package com.iecho.main;

import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.webservice.WebServiceConnection;

public class ForgotPasswordActivity extends Activity {
	private Button submitButton,homeButton;
	private EditText emailEditText;
	private Context context;
	private ProgressDialog progressDialog;
	private Handler forgotPasswordHandler;
	private WebServiceConnection serviceConnection;
	private HashMap<String, String> responseHashMap;
	private String status="",statusMsg="";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_password);
		context=this;
		setUpViews();

		Button infoButton=(Button)findViewById(R.id.forgot_pass_info);
		infoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent infoIntent=new Intent(context, AboutIechoActivity.class);
				startActivity(infoIntent);
			}
		});

		homeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		forgotPasswordHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				try{
					if(progressDialog!=null){
						progressDialog.dismiss();
					}
					responseHashMap=serviceConnection.serviceResponce();
					if(responseHashMap!=null){
						status=responseHashMap.get("status");
						statusMsg=responseHashMap.get("statusMsg");
					}
					if(status.equals("false")){
						Toast.makeText(context, "Invalid user details", 3000).show();	
					}else{
						Toast.makeText(context, "Email successfully sent", 3000).show();
						finish();
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(new ApplicationUtility(context).checkNetworkConnection()){
					final String email=emailEditText.getText().toString();
					if (email.equals("")) {
						new ApplicationUtility(context, ForgotPasswordActivity.this).errorDialogAlertBox("Please enter email id.", "Forgot Password Error!", R.drawable.icon);
					} else {
						//hide open keypad
						InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(emailEditText.getWindowToken(), 0);

						progressDialog=ProgressDialog.show(context, "", "please wait...");
						try{
							new Thread(){
								@Override
								public void run() {
									serviceConnection=new WebServiceConnection("FORGOT_PASSWORD", "http://tempuri.org/IiEchoMobileService/ForgotPassword", "http://tempuri.org/", "ForgotPassword", ApplicationUtility.SERVICE_URL,"email",email);
									forgotPasswordHandler.sendEmptyMessage(0);
								}
							}.start();
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				}else{
					Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	private void setUpViews(){
		submitButton=(Button)findViewById(R.id.forgot_password_submit_btn);
		homeButton=(Button)findViewById(R.id.forgot_password_home_btn);
		emailEditText=(EditText)findViewById(R.id.forgot_password_email_edit);
	}
}
