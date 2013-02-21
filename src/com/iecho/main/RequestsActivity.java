package com.iecho.main;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.webservice.WebServiceConnection;

public class RequestsActivity extends Activity {
	private Button myRequests,friendRequests,makeRequests,addAFriend/*,pendingFriendRequestsButton,newFriendRequestButton*/;
	public Context context;
	private ProgressDialog progressDialog;
	private WebServiceConnection serviceConnection;
	private Handler requestHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.requests);
		context=this;
		setUpViews();

		Button infoButton=(Button)findViewById(R.id.requests_info);
		infoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent infoIntent=new Intent(context, AboutIechoActivity.class);
				startActivity(infoIntent);
			}
		});

		myRequests.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent myPendingRequestIntent=new Intent(context, MyPendingRequestsActivity.class);
				startActivityForResult(myPendingRequestIntent, 402);
			}
		});

		friendRequests.setOnClickListener(new OnClickListener() {

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

		makeRequests.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent makeNewRequestIntent=new Intent(context,MakeRequestActivity.class);
				startActivityForResult(makeNewRequestIntent, 412);
			}
		});
		
		addAFriend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				final Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.custom_dialog);
				dialog.setTitle("Make New Friend Request");
				dialog.getWindow().setTitleColor(Color.parseColor("#008bd0"));
				dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
				Button button1 = (Button) dialog.findViewById(R.id.dialog_btn1);
				button1.setText("Send request by email");
				Button button2= (Button) dialog.findViewById(R.id.dialog_btn2);
				button2.setText("Send request to mobile phone");
				Button sendRequestFromDeviceButton= (Button) dialog.findViewById(R.id.dialog_btn3);
				sendRequestFromDeviceButton.setText("Send request from device contacts");
				Button button4 = (Button) dialog.findViewById(R.id.dialog_btn4);
				button4.setText("Cancel");
				

				button1.setVisibility(View.VISIBLE);
				button2.setVisibility(View.VISIBLE);
				sendRequestFromDeviceButton.setVisibility(View.VISIBLE);
				button4.setVisibility(View.VISIBLE);

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
											@Override
											public void run() {
												serviceConnection=new WebServiceConnection("SEND_NEW_FRIEND_REQUEST", "http://tempuri.org/IiEchoMobileService/SendNewFriendRequest", "http://tempuri.org/", "SendNewFriendRequest", ApplicationUtility.SERVICE_URL, "customerId", "email", "mobileNo", "CountryCode" , ApplicationUtility.USER_ID, emailId, "","1");
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
						dialog_inner_2.setTitle("Send request to mobile phone");
						dialog_inner_2.getWindow().setTitleColor(Color.parseColor("#008bd0"));
						dialog_inner_2.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
						TextView text1 = (TextView) dialog_inner_2.findViewById(R.id.dialog2_txt1);
						text1.setText("Enter mobile number");
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
									Toast.makeText(context, "Please enter mobile number", 3000).show();
								}else{
									if(new ApplicationUtility(context).checkNetworkConnection()){
										progressDialog=ProgressDialog.show(context, "", "please wait...");
										new Thread(){
											@Override
											public void run() {
												serviceConnection=new WebServiceConnection("SEND_NEW_FRIEND_REQUEST", "http://tempuri.org/IiEchoMobileService/SendNewFriendRequest", "http://tempuri.org/", "SendNewFriendRequest", ApplicationUtility.SERVICE_URL, "customerId", "email", "mobileNo","CountryCode" , ApplicationUtility.USER_ID, "", phoneNo,"1");
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
				
				sendRequestFromDeviceButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent=new Intent(RequestsActivity.this,DeviceContactsActivity.class);
						startActivityForResult(intent, 455);
						dialog.dismiss();
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
		});
	}

	private void setUpViews() {
		myRequests = (Button) findViewById(R.id.requests_home_btn);
		friendRequests= (Button) findViewById(R.id.requests_view_track_req_btn);
		makeRequests= (Button) findViewById(R.id.requests_make_track_req_btn);
		addAFriend= (Button) findViewById(R.id.requests_make_track_req_btn);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode==402) {
			finish();
		}else if (resultCode==455) {
			final Dialog dialog_inner_2 = new Dialog(context);
			dialog_inner_2.setContentView(R.layout.custom_dialog_editbox);
			dialog_inner_2.setTitle("Send request");
			dialog_inner_2.getWindow().setTitleColor(Color.parseColor("#008bd0"));
			dialog_inner_2.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
			TextView text1 = (TextView) dialog_inner_2.findViewById(R.id.dialog2_txt1);
			text1.setText("mobile number");
			text1.setTextColor(Color.parseColor("#008bd0"));
			final EditText edit1= (EditText) dialog_inner_2.findViewById(R.id.dialog2_edit1);
			edit1.setText(data.getExtras().getString("number"));
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
						Toast.makeText(context, "Please enter mobile number", 3000).show();
					}else{
						if(new ApplicationUtility(context).checkNetworkConnection()){
							progressDialog=ProgressDialog.show(context, "", "please wait...");
							new Thread(){
								@Override
								public void run() {
									serviceConnection=new WebServiceConnection("SEND_NEW_FRIEND_REQUEST", "http://tempuri.org/IiEchoMobileService/SendNewFriendRequest", "http://tempuri.org/", "SendNewFriendRequest", ApplicationUtility.SERVICE_URL, "customerId", "email", "mobileNo","CountryCode" , ApplicationUtility.USER_ID, "", phoneNo,"1");
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

		}
	}

	}


