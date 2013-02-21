package com.iecho.main;

import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.database.DataBaseConnectionManager;
import com.iecho.services.SendUpdatedLocationService;
import com.iecho.webservice.WebServiceConnection;

public class ViewPendingRequestActivity extends Activity {
	private Button homeButton,myPendingRequestButton,friendingPendingRequestButton,friendMePendingRequestButton;
	private Context context;
	private ProgressDialog progressDialog;
	private Handler requestHandler;
	private HashMap<String, String> responseHashMap;
	private WebServiceConnection serviceConnection;
	private String status="",statusMsg="";
	private DataBaseConnectionManager connectionManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_pending_requests);
		context=this;
		setUpViews();

		Button infoButton=(Button)findViewById(R.id.viewpending_req_info);
		infoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent infoIntent=new Intent(context, AboutIechoActivity.class);
				startActivity(infoIntent);
			}
		});

		requestHandler=new Handler(){
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

					Toast.makeText(context, statusMsg, 3000).show();

				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		homeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(402);
				finish();
			}
		});

		myPendingRequestButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent myPendingRequestIntent=new Intent(context, MyPendingRequestsActivity.class);
				startActivityForResult(myPendingRequestIntent, 402);
			}
		});

		friendingPendingRequestButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(new ApplicationUtility(context).checkNetworkConnection()){
					Intent friendRequestIntent=new Intent(context,FriendRequestActivity.class);
					startActivityForResult(friendRequestIntent, 418);
				}else{
					Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
				}
			}
		});

		friendMePendingRequestButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				connectionManager=new DataBaseConnectionManager(context);

				final Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.custom_dialog);
				dialog.setTitle("Make New Friend Request");
				dialog.getWindow().setTitleColor(Color.parseColor("#008bd0"));
				dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
				Button button1 = (Button) dialog.findViewById(R.id.dialog_btn1);
				button1.setText("Send request by email");
				Button button2= (Button) dialog.findViewById(R.id.dialog_btn2);
				button2.setText("Send request by phone number");

				button1.setVisibility(View.VISIBLE);
				button2.setVisibility(View.VISIBLE);

				button1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						final Dialog dialog_inner_1 = new Dialog(context);
						dialog_inner_1.setContentView(R.layout.custom_dialog_editbox);
						dialog_inner_1.setTitle("Send request by email          ");
						dialog_inner_1.getWindow().setTitleColor(Color.parseColor("#008bd0"));
						dialog_inner_1.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
						TextView text1 = (TextView) dialog_inner_1.findViewById(R.id.dialog2_txt1);
						text1.setText("Enter email id");
						text1.setTextColor(Color.parseColor("#008bd0"));
						final EditText edit1= (EditText) dialog_inner_1.findViewById(R.id.dialog2_edit1);
						Button button1= (Button) dialog_inner_1.findViewById(R.id.dialog2_btn1);		
						button1.setText("Send Invite");
						Button button2= (Button) dialog_inner_1.findViewById(R.id.dialog2_btn2);	
						button2.setText("Cancel");
						button1.setVisibility(View.VISIBLE);
						button2.setVisibility(View.VISIBLE);
						edit1.setVisibility(View.VISIBLE);
						text1.setVisibility(View.VISIBLE);
						button1.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								final String emailId=edit1.getText().toString();
								if(emailId.equals("")){
									Toast.makeText(context, "Please enter email id", 3000).show();
								}else{
									if(new ApplicationUtility(context).checkNetworkConnection()){
										progressDialog=ProgressDialog.show(context, "", "please wait...");
										new Thread(){
											public void run() {
												serviceConnection=new WebServiceConnection("SEND_NEW_FRIEND_REQUEST", "http://tempuri.org/IiEchoMobileService/SendNewFriendRequest", "http://tempuri.org/", "SendNewFriendRequest", ApplicationUtility.SERVICE_URL, "customerId", "email", "mobileNo", "CountryCode",ApplicationUtility.USER_ID, emailId, "","1");
												requestHandler.sendEmptyMessage(0);
											};
										}.start();
										dialog_inner_1.dismiss();
									}else{
										Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
									}
								}
							}
						});
						button2.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog_inner_1.dismiss();
							}
						});
						dialog_inner_1.show();

						dialog.dismiss();
					}
				});
				button2.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						final Dialog dialog_inner_2 = new Dialog(context);
						dialog_inner_2.setContentView(R.layout.custom_dialog_editbox);
						dialog_inner_2.setTitle("Send request by phone number");
						dialog_inner_2.getWindow().setTitleColor(Color.parseColor("#008bd0"));
						dialog_inner_2.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
						TextView text1 = (TextView) dialog_inner_2.findViewById(R.id.dialog2_txt1);
						text1.setText("Enter phone number");
						text1.setTextColor(Color.parseColor("#008bd0"));
						final EditText edit1= (EditText) dialog_inner_2.findViewById(R.id.dialog2_edit1);
						Button button1= (Button) dialog_inner_2.findViewById(R.id.dialog2_btn1);	
						button1.setText("Send Invite");
						Button button2= (Button) dialog_inner_2.findViewById(R.id.dialog2_btn2);	
						button2.setText("Cancel");
						button1.setVisibility(View.VISIBLE);
						button2.setVisibility(View.VISIBLE);
						edit1.setVisibility(View.VISIBLE);
						edit1.setInputType(InputType.TYPE_CLASS_NUMBER);
						text1.setVisibility(View.VISIBLE);
						button1.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								final String phoneNo=edit1.getText().toString();
								if(phoneNo.equals("")){
									Toast.makeText(context, "Please enter phone number", 3000).show();
								}else{
									if(new ApplicationUtility(context).checkNetworkConnection()){
										progressDialog=ProgressDialog.show(context, "", "please wait...");
										new Thread(){
											public void run() {
												serviceConnection=new WebServiceConnection("SEND_NEW_FRIEND_REQUEST", "http://tempuri.org/IiEchoMobileService/SendNewFriendRequest", "http://tempuri.org/", "SendNewFriendRequest", ApplicationUtility.SERVICE_URL, "customerId", "email", "mobileNo","CountryCode" ,ApplicationUtility.USER_ID, "", phoneNo,"1");
												requestHandler.sendEmptyMessage(0);
											};
										}.start();
										dialog_inner_2.dismiss();
									}else{
										Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
									}
								}
							}
						});
						button2.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog_inner_2.dismiss();
							}
						});
						dialog_inner_2.show();

						dialog.dismiss();
					}
				});
				dialog.show();

			}
		});
	}

	private void setUpViews(){
		homeButton=(Button)findViewById(R.id.view_pending_requests_home_btn);
		myPendingRequestButton=(Button)findViewById(R.id.view_pending_requests_my_req_btn);
		friendingPendingRequestButton=(Button)findViewById(R.id.view_pending_requests_friend_req_btn);
		friendMePendingRequestButton=(Button)findViewById(R.id.view_pending_requests_friend_me_req_btn);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode==402) {
			finish();
		}else if (resultCode==404) {
			setResult(402);
			finish();
		}else if (resultCode==4182) {
			setResult(402);
			finish();
		}
	}

	private Intent locationServiceIntent;

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
