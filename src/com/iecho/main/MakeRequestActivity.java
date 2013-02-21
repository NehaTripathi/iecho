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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.database.DataBaseConnectionManager;
import com.iecho.webservice.WebServiceConnection;

public class MakeRequestActivity extends Activity {
	private Button homeButton,makeTrackRequestButton,followMeRequestButton,newFriendRequestButton;
	private Context context;
	private ProgressDialog progressDialog;
	private Handler requestHandler;
	private WebServiceConnection serviceConnection;
	private HashMap<String, String> responseHashMap;
	private String status="",statusMsg="";
	private DataBaseConnectionManager connectionManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.make_request);
		context=this;
		setUpViews();
		Button infoButton=(Button)findViewById(R.id.make_request_info);
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
				setResult(402);
				finish();
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

		/*makeTrackRequestButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent contectIntent=new Intent(context, ContactsActivity.class);
				contectIntent.putExtra("REQUEST_NAME", "TrackRequest");
				startActivityForResult(contectIntent, 504);
			}
		});

		followMeRequestButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent contectIntent=new Intent(context, ContactsActivity.class);
				contectIntent.putExtra("REQUEST_NAME", "FollowRequest");
				startActivityForResult(contectIntent, 504);
			}
		});*/


		newFriendRequestButton.setOnClickListener(new OnClickListener() {

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
				Button button3 = (Button) dialog.findViewById(R.id.dialog_btn3);
				button3.setText("Cancel");

				button1.setVisibility(View.VISIBLE);
				button2.setVisibility(View.VISIBLE);
				button3.setVisibility(View.VISIBLE);

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
												serviceConnection=new WebServiceConnection("SEND_NEW_FRIEND_REQUEST", "http://tempuri.org/IiEchoMobileService/SendNewFriendRequest", "http://tempuri.org/", "SendNewFriendRequest", ApplicationUtility.SERVICE_URL, "customerId", "email", "mobileNo","CountryCode", ApplicationUtility.USER_ID, emailId, "","1");
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
											public void run() {
												serviceConnection=new WebServiceConnection("SEND_NEW_FRIEND_REQUEST", "http://tempuri.org/IiEchoMobileService/SendNewFriendRequest", "http://tempuri.org/", "SendNewFriendRequest", ApplicationUtility.SERVICE_URL, "customerId", "email", "mobileNo", "CountryCode" ,ApplicationUtility.USER_ID, "", phoneNo,"1");
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
				button3.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.show();

			}
		});

	}

	private void setUpViews(){
		homeButton=(Button)findViewById(R.id.make_requests_home_btn);
		makeTrackRequestButton=(Button)findViewById(R.id.make_requests_track_req_btn);
		followMeRequestButton=(Button)findViewById(R.id.make_requests_follow_me_req_btn);
		newFriendRequestButton=(Button)findViewById(R.id.make_requests_new_friend_req_btn);
	}
}
