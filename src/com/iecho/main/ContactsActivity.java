package com.iecho.main;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.iecho.adapters.SeparatedListAdapter;
import com.iecho.apputil.ApplicationUtility;
import com.iecho.database.DataBaseConnectionManager;
import com.iecho.vo.ContactVO;
import com.iecho.webservice.WebServiceConnection;

public class ContactsActivity extends Activity {
	private ListView contactListView;
	private Button homeButton,addGroupButton,searchContactButton;
	private Context context;
	private DataBaseConnectionManager connectionManager;
	private static String[] groupArray;
	private SeparatedListAdapter adapter;
	private Handler searchHandler,removeFriendHandler,updateFriendStateHandler,comfirmDeleteHandler;
	private Dialog dialog_inner_1;
	private Dialog dialog_inner_2;
	private ArrayList<ArrayList> arrayLists;
	public String firstName[],lastName[],email[],phoneNumber[],userId[];
	public Bitmap[] resultImgBitmap;
	//	private String[] idArray;
	private ArrayList<String> list2;
	private Intent contactResultIntent;
	private Intent intent;
	private String requestName="";
	private ProgressDialog progressDialog;
	private Handler handler,refreshContectListHandler,updateWebContactsHandler;
	private WebServiceConnection serviceConnection,changeStateServiceConnection,webContectServiceConnection;
	private HashMap<String, String> serviceResponce;
	private String status="",message="";
	@SuppressWarnings("rawtypes")
	private ArrayList list;
	private ProgressDialog removeContactProgressDialog;
	private String toBeDeleteUserId="";
	private String friendIdUpdateGroup="";
	private String destination="",time="",startPoint="";
	private JSONObject responseJsonObject,webContectJsonObject;
	private String[] usersId;
	private String[] fName,lName,contactNumber,emailId;
	private String toDeleteUID="";
	private String f_name="",l_name="",number="",type="";
	private String user_id="", email_="", image_url="";
	private int totalContacts=0;
	private TextView headerTxt;
	private ListViewCustomAdapter listViewCustomAdapter;
	private HashMap<String, String> responseHashMap;
	private String statusMsg="";
	private byte[] friendImageByteArray;
	private ArrayList<byte[]> imageArrayList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts);
		context=this;
		setUpViews();
		imageArrayList=new ArrayList<byte[]>();
		Button infoButton=(Button)findViewById(R.id.contects_info);
		infoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent infoIntent=new Intent(context, AboutIechoActivity.class);
				startActivity(infoIntent);
			}
		});

		intent=getIntent();
		Bundle bundle=intent.getExtras();
		try{
			requestName=bundle.getString("REQUEST_NAME");
		}catch (Exception e) {
			e.printStackTrace();
		}
		try{
			destination=bundle.getString("destination");
			System.out.println("destination>>>>>>>>>>>>> "+destination);
		}catch (Exception e) {
			e.printStackTrace();
		}
		try{
			time=bundle.getString("time");
			System.out.println("time>>>>>>>>>>>>>>>"+time);
		}catch (Exception e) {
			e.printStackTrace();
		}
		try{
			startPoint=bundle.getString("startPoint");
			System.out.println("startPoint>>>>>>>>>>>>>>>"+startPoint);
		}catch (Exception e) {
			e.printStackTrace();
		}

		//add the header text to screen vip
		headerTxt=(TextView) findViewById(R.id.header_scr);

		connectionManager=new DataBaseConnectionManager(context);

		try {
			loadContactList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(requestName.equals("")){
			System.out.println("Loading contact list===========");
			if (totalContacts==0) {
				Toast.makeText(context, "No contacts available. Press menu to add some new friends", Toast.LENGTH_LONG).show();
			}
		}else{
			//setting the header with screen 
			if(requestName.equals("FollowRequest")){
				headerTxt.setText("Follow You");
			}else if(requestName.equals("TrackRequest")){
				headerTxt.setText("Find Me");
			}else if(requestName.equals("SendNotification")){
				headerTxt.setText("Send Destination");
			}


			if (totalContacts!=0) {
				Toast.makeText(context, "Please pick a contact", Toast.LENGTH_LONG).show();	
			}else{
				Toast.makeText(context, "No contacts available. Press menu to add some new friends", Toast.LENGTH_LONG).show();
			}
		}


		refreshContectListHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(progressDialog!=null){
					progressDialog.dismiss();
				}
				try {
					loadContactList();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};

		updateWebContactsHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				try {
					afterResponse();
				} catch (Exception e) {
					e.printStackTrace();
				}

			};
		};

		comfirmDeleteHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				Toast.makeText(context, "Group deleted successfully", Toast.LENGTH_SHORT).show();
			}
		};

		removeFriendHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(removeContactProgressDialog!=null){
					removeContactProgressDialog.dismiss();
				}
				try {
					Toast.makeText(context, "Contact deleted", Toast.LENGTH_SHORT).show();
					connectionManager.deleteContact(Long.parseLong(toBeDeleteUserId));
					toBeDeleteUserId="";
					loadContactList();	
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};

		handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(progressDialog!=null){
					progressDialog.dismiss();
				}

				try {
					serviceResponce=serviceConnection.serviceResponce();
					if(serviceResponce!=null){
						status=serviceResponce.get("status");
						message=serviceResponce.get("statusMsg");
						if(status.equals("false")){
							Toast.makeText(context, "Request not sent", Toast.LENGTH_LONG).show();
						}if(status.equals("true")){
							if (message.equals("Valid")) {
								Toast.makeText(context, "Request sent successfully", Toast.LENGTH_LONG).show();
							}else if(message.equals("SMS send.")){
								Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_LONG).show();
							}else {
								Toast.makeText(context, message, Toast.LENGTH_LONG).show();
							}
						}
					}				
				} catch (Exception e) {
					e.printStackTrace();
				}
				ContactsActivity.this.finish();
			}
		};

		searchHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(dialog_inner_1!=null){
					dialog_inner_1.dismiss();
					System.out.println("array list length : "+arrayLists.size());
					if(arrayLists.size()!=0){
						firstName=new String[arrayLists.size()];
						lastName=new String[arrayLists.size()];
						phoneNumber=new String[arrayLists.size()];
						email=new String[arrayLists.size()];
						userId=new String[arrayLists.size()];
						resultImgBitmap=new Bitmap[arrayLists.size()];
					}

					for (int i = 0; i < arrayLists.size(); i++) {
						ArrayList innerArrayList=arrayLists.get(i);
						System.out.println(innerArrayList.get(0));
						System.out.println(innerArrayList.get(1));
						System.out.println(innerArrayList.get(2));
						System.out.println(innerArrayList.get(3));
						firstName[i]=innerArrayList.get(0).toString();
						lastName[i]=innerArrayList.get(1).toString();
						phoneNumber[i]=innerArrayList.get(2).toString();
						email[i]=innerArrayList.get(3).toString();
						userId[i]=innerArrayList.get(4).toString();
						resultImgBitmap[i]=(Bitmap)innerArrayList.get(5);
					}
					contactResultIntent=new Intent(context,ContactSearchResultActivity.class);
					contactResultIntent.putExtra("fNameArray", firstName);
					contactResultIntent.putExtra("lNameArray", lastName);
					contactResultIntent.putExtra("numberArray", phoneNumber);
					contactResultIntent.putExtra("emailArray", email);
					contactResultIntent.putExtra("userIdArray", userId);
					//					contactResultIntent.putExtra("userImageArray", resultImgBitmap);
					contactResultIntent.putExtra("requestName", requestName);
					if(requestName.equals("SendNotification")){
						contactResultIntent.putExtra("startPoint", startPoint);
						contactResultIntent.putExtra("destination", destination);
						contactResultIntent.putExtra("time", time);
					}
				}
				if(dialog_inner_2!=null){
					dialog_inner_2.dismiss();
					System.out.println("array list length : "+arrayLists.size());
					if(arrayLists.size()!=0){
						firstName=new String[arrayLists.size()];
						lastName=new String[arrayLists.size()];
						phoneNumber=new String[arrayLists.size()];
						email=new String[arrayLists.size()];
						userId=new String[arrayLists.size()];
						resultImgBitmap=new Bitmap[arrayLists.size()];
					}

					for (int i = 0; i < arrayLists.size(); i++) {
						ArrayList innerArrayList=arrayLists.get(i);
						System.out.println(innerArrayList.get(0).toString());
						System.out.println(innerArrayList.get(1).toString());
						System.out.println(innerArrayList.get(2).toString());
						System.out.println(innerArrayList.get(3).toString());
						firstName[i]=innerArrayList.get(0).toString();
						lastName[i]=innerArrayList.get(1).toString();
						phoneNumber[i]=innerArrayList.get(2).toString();
						email[i]=innerArrayList.get(3).toString();
						userId[i]=innerArrayList.get(4).toString();
						resultImgBitmap[i]=(Bitmap)innerArrayList.get(5);
					}
					contactResultIntent=new Intent(context,ContactSearchResultActivity.class);
					contactResultIntent.putExtra("fNameArray", firstName);
					contactResultIntent.putExtra("lNameArray", lastName);
					contactResultIntent.putExtra("numberArray", phoneNumber);
					contactResultIntent.putExtra("emailArray", email);
					contactResultIntent.putExtra("userIdArray", userId);
					//					contactResultIntent.putExtra("userImageArray", resultImgBitmap);
					contactResultIntent.putExtra("requestName", requestName);
					if(requestName.equals("SendNotification")){
						contactResultIntent.putExtra("startPoint", startPoint);
						contactResultIntent.putExtra("destination", destination);
						contactResultIntent.putExtra("time", time);
					}
				}
				if(arrayLists.size()!=0){
					startActivityForResult(contactResultIntent, 455);
				}else{
					Toast.makeText(context, "No result found", Toast.LENGTH_LONG).show();
				}
			}
		};

		updateFriendStateHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(progressDialog!=null){
					progressDialog.dismiss();
				}
				try {
					friendIdUpdateGroup="";
					loadContactList();	
				} catch (Exception e) {
					e.printStackTrace();
				}
				Toast.makeText(context, "Contact type changed successfully", Toast.LENGTH_LONG).show();
			}
		};


		searchContactButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.custom_dialog);
				dialog.setTitle("Search                                  ");
				dialog.getWindow().setTitleColor(Color.parseColor("#008bd0"));
				dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
				Button button1 = (Button) dialog.findViewById(R.id.dialog_btn1);
				button1.setText("Search by e-mail");
				Button button2= (Button) dialog.findViewById(R.id.dialog_btn2);
				button2.setText("Search by first name");
				Button button3= (Button) dialog.findViewById(R.id.dialog_btn3);
				button3.setText("Search by last name");
				Button button4= (Button) dialog.findViewById(R.id.dialog_btn4);
				button4.setText("Search by number");
				Button button5= (Button) dialog.findViewById(R.id.dialog_btn5);
				button5.setText("Cancel");

				button1.setVisibility(View.VISIBLE);
				button2.setVisibility(View.VISIBLE);
				button3.setVisibility(View.VISIBLE);
				button4.setVisibility(View.VISIBLE);
				button5.setVisibility(View.VISIBLE);

				button1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						dialog_inner_1 = new Dialog(context);
						dialog_inner_1.setContentView(R.layout.custom_dialog_editbox);
						dialog_inner_1.setTitle("Search by e-mail                   ");
						dialog_inner_1.getWindow().setTitleColor(Color.parseColor("#008bd0"));
						dialog_inner_1.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
						TextView text1 = (TextView) dialog_inner_1.findViewById(R.id.dialog2_txt1);
						text1.setText("Enter email id");
						text1.setTextColor(Color.parseColor("#008bd0"));
						final EditText edit1= (EditText) dialog_inner_1.findViewById(R.id.dialog2_edit1);
						Button button1= (Button) dialog_inner_1.findViewById(R.id.dialog2_btn1);		
						button1.setText("Search");
						Button button2= (Button) dialog_inner_1.findViewById(R.id.dialog2_btn2);	
						button2.setText("Cancel");
						button1.setVisibility(View.VISIBLE);
						button2.setVisibility(View.VISIBLE);
						edit1.setVisibility(View.VISIBLE);
						edit1.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
						text1.setVisibility(View.VISIBLE);
						button1.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								final String searchString=edit1.getText().toString();
								if(searchString.equals("")){
									Toast.makeText(context, "Please enter search string", Toast.LENGTH_LONG).show()	;
								}else{
									new Thread(){
										@Override
										public void run() {
											arrayLists = connectionManager.getSearchContactResult("contact_email",searchString,new String[]{"contact_first_name","contact_last_name","contact_number","contact_email","user_id","friend_image"});
											searchHandler.sendEmptyMessage(0);
										};	
									}.start();
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

						dialog_inner_2 = new Dialog(context);
						dialog_inner_2.setContentView(R.layout.custom_dialog_editbox);
						dialog_inner_2.setTitle("Search by first name               ");
						dialog_inner_2.getWindow().setTitleColor(Color.parseColor("#008bd0"));
						dialog_inner_2.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
						TextView text1 = (TextView) dialog_inner_2.findViewById(R.id.dialog2_txt1);
						text1.setText("Enter first name");
						text1.setTextColor(Color.parseColor("#008bd0"));
						final EditText edit1= (EditText) dialog_inner_2.findViewById(R.id.dialog2_edit1);
						Button button1= (Button) dialog_inner_2.findViewById(R.id.dialog2_btn1);	
						button1.setText("Search");
						Button button2= (Button) dialog_inner_2.findViewById(R.id.dialog2_btn2);	
						button2.setText("Cancel");
						button1.setVisibility(View.VISIBLE);
						button2.setVisibility(View.VISIBLE);
						edit1.setVisibility(View.VISIBLE);
						edit1.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
						text1.setVisibility(View.VISIBLE);
						button1.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								final String searchString=edit1.getText().toString();
								if(searchString.equals("")){
									Toast.makeText(context, "Please enter search string", Toast.LENGTH_LONG).show()	;
								}else{
									new Thread(){
										@Override
										public void run() {
											arrayLists=	connectionManager.getSearchContactResult("contact_first_name",searchString,new String[]{"contact_first_name","contact_last_name","contact_number","contact_email","user_id","friend_image"});
											searchHandler.sendEmptyMessage(0);
										};	
									}.start();
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

						dialog_inner_2 = new Dialog(context);
						dialog_inner_2.setContentView(R.layout.custom_dialog_editbox);
						dialog_inner_2.setTitle("Search by last name                ");
						dialog_inner_2.getWindow().setTitleColor(Color.parseColor("#008bd0"));
						dialog_inner_2.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
						TextView text1 = (TextView) dialog_inner_2.findViewById(R.id.dialog2_txt1);
						text1.setText("Enter last name");
						text1.setTextColor(Color.parseColor("#008bd0"));
						final EditText edit1= (EditText) dialog_inner_2.findViewById(R.id.dialog2_edit1);
						Button button1= (Button) dialog_inner_2.findViewById(R.id.dialog2_btn1);	
						button1.setText("Search");
						Button button2= (Button) dialog_inner_2.findViewById(R.id.dialog2_btn2);	
						button2.setText("Cancel");
						button1.setVisibility(View.VISIBLE);
						button2.setVisibility(View.VISIBLE);
						edit1.setVisibility(View.VISIBLE);
						edit1.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
						text1.setVisibility(View.VISIBLE);
						button1.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								final String searchString=edit1.getText().toString();
								if(searchString.equals("")){
									Toast.makeText(context, "Please enter search string", Toast.LENGTH_LONG).show()	;
								}else{
									new Thread(){
										@Override
										public void run() {
											arrayLists=	connectionManager.getSearchContactResult("contact_last_name",searchString,new String[]{"contact_first_name","contact_last_name","contact_number","contact_email","user_id","friend_image"});
											searchHandler.sendEmptyMessage(0);
										};	
									}.start();
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
				button4.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						dialog_inner_2 = new Dialog(context);
						dialog_inner_2.setContentView(R.layout.custom_dialog_editbox);
						dialog_inner_2.setTitle("Search by number                   ");
						dialog_inner_2.getWindow().setTitleColor(Color.parseColor("#008bd0"));
						dialog_inner_2.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
						TextView text1 = (TextView) dialog_inner_2.findViewById(R.id.dialog2_txt1);
						text1.setText("Enter phone number");
						text1.setTextColor(Color.parseColor("#008bd0"));
						final EditText edit1= (EditText) dialog_inner_2.findViewById(R.id.dialog2_edit1);
						Button button1= (Button) dialog_inner_2.findViewById(R.id.dialog2_btn1);	
						button1.setText("Search");
						Button button2= (Button) dialog_inner_2.findViewById(R.id.dialog2_btn2);	
						button2.setText("Cancel");
						button1.setVisibility(View.VISIBLE);
						button2.setVisibility(View.VISIBLE);
						edit1.setVisibility(View.VISIBLE);
						edit1.setInputType(InputType.TYPE_CLASS_NUMBER);
						edit1.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
						text1.setVisibility(View.VISIBLE);
						button1.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								final String searchString=edit1.getText().toString();
								if(searchString.equals("")){
									Toast.makeText(context, "Please enter search string", Toast.LENGTH_LONG).show()	;
								}else{
									new Thread(){
										@Override
										public void run() {
											arrayLists=connectionManager.getSearchContactResult("contact_number",searchString,new String[]{"contact_first_name","contact_last_name","contact_number","contact_email","user_id","friend_image"});
											searchHandler.sendEmptyMessage(0);
										};	
									}.start();
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
				button5.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
				dialog.show();


			}
		});

		homeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		addGroupButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final Dialog dialog_inner_1 = new Dialog(context);
				dialog_inner_1.setContentView(R.layout.custom_dialog_editbox);
				dialog_inner_1.setTitle("Create New Contacts Group   ");
				dialog_inner_1.getWindow().setTitleColor(Color.parseColor("#008bd0"));
				dialog_inner_1.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
				TextView text1 = (TextView) dialog_inner_1.findViewById(R.id.dialog2_txt1);
				text1.setText("Enter Group Name");
				text1.setTextColor(Color.parseColor("#008bd0"));
				final EditText edit1= (EditText) dialog_inner_1.findViewById(R.id.dialog2_edit1);
				Button button1= (Button) dialog_inner_1.findViewById(R.id.dialog2_btn1);		
				button1.setText("Create Group");
				Button button2= (Button) dialog_inner_1.findViewById(R.id.dialog2_btn2);	
				button2.setText("Cancel");
				button1.setVisibility(View.VISIBLE);
				button2.setVisibility(View.VISIBLE);
				edit1.setVisibility(View.VISIBLE);
				text1.setVisibility(View.VISIBLE);
				button1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String groupName=edit1.getText().toString();
						if (groupName.equals("")) {
							Toast.makeText(context, "Please enter group name", 3000).show();
						}	else{
							if(groupName.startsWith(" ")){
								Toast.makeText(context, "Please enter valid group name", Toast.LENGTH_LONG).show();
							}else{
								boolean state=false;
								for (String element : groupArray) {
									if(groupName.equalsIgnoreCase(element)||groupName.equalsIgnoreCase("My Group")){
										state=true;
									}else{
										state=false;
									}
								}
								if (state) {
									Toast.makeText(context, "Group name already exists", Toast.LENGTH_LONG).show();
								} else {
									System.out.println("group name is : "+groupName);
									double group_id=Math.random();
									group_id=group_id*10000;
									System.out.println("group id is >>>>>>>>>>>>> "+group_id);
									long row_id=connectionManager.addNewGroup(groupName, (int)group_id);
									System.out.println(">>>>>>> row id is >>>>>> "+row_id);
									Toast.makeText(context, "Group created successfully", 3000).show();
									try{
										loadContactList();
									}catch (Exception e) {
										e.printStackTrace();
									}
									dialog_inner_1.dismiss();
								}
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

	}
	
	private void showChoiceDialog(int position){
		long id=adapter.getItemId(position);
		final String friendID=list2.get((int)id);
		if (!requestName.equals("")) {
			final Dialog dialog = new Dialog(context);
			dialog.setContentView(R.layout.custom_dialog);
			dialog.setTitle("Send Request Confirm             ");
			dialog.getWindow().setTitleColor(Color.parseColor("#008bd0"));
			dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
			Button button1 = (Button) dialog.findViewById(R.id.dialog_btn1);
			button1.setText("Send Request");
			Button button2= (Button) dialog.findViewById(R.id.dialog_btn2);
			button2.setText("Remove Contact");
			Button button3= (Button) dialog.findViewById(R.id.dialog_btn3);
			button3.setText("Cancel");

			button1.setVisibility(View.VISIBLE);
			try{
				if(ApplicationUtility.USER_CURRENT_STATE.equals("Private")){
					button2.setVisibility(View.VISIBLE);
				}else{
					button2.setVisibility(View.GONE);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			button3.setVisibility(View.VISIBLE);

			/**
			 * follow you request
			 */
			button1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(new ApplicationUtility(context).checkNetworkConnection()){
						dialog.dismiss();
						if (friendID.equals("")) {
							if(requestName.equals("FollowRequest")){
								Toast.makeText(context, "Can not send Follow You request to web contact", Toast.LENGTH_LONG).show();
							}else if (requestName.equals("TrackRequest")) {
								Toast.makeText(context, "Can not send Find Me request to web contact", Toast.LENGTH_LONG).show();
							}else if (requestName.equals("SendNotification")) {
								Toast.makeText(context, "Can not send Notification to web contact", Toast.LENGTH_LONG).show();
							}
						} else {
							progressDialog=ProgressDialog.show(context, "", "please wait...");
							new Thread(){
								@Override
								public void run() {
									if(requestName.equals("FollowRequest")){
										serviceConnection=new WebServiceConnection("FOLLOW_YOU_REQUEST","http://tempuri.org/IiEchoMobileService/FollowYou", "http://tempuri.org/", "FollowYou", ApplicationUtility.SERVICE_URL, "customerId", "friendId", ApplicationUtility.USER_ID, friendID);
									}else if (requestName.equals("TrackRequest")) {
										serviceConnection=new WebServiceConnection("FIND_ME_REQUEST","http://tempuri.org/IiEchoMobileService/FindMe", "http://tempuri.org/", "FindMe", ApplicationUtility.SERVICE_URL, "customerId", "friendId", ApplicationUtility.USER_ID, friendID);	
									}else if (requestName.equals("SendNotification")) {
										serviceConnection=new WebServiceConnection("SEND_NOTIFICATION","http://tempuri.org/IiEchoMobileService/SendNotification", "http://tempuri.org/", "SendNotification", ApplicationUtility.SERVICE_URL, "userid", "friendid","startPoint","destination","time", "CountryCode" ,ApplicationUtility.USER_ID, friendID,startPoint,destination,time,"1");	
									}
									handler.sendEmptyMessage(0);
								}
							}.start();
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
					if (friendID.equals("")) {
						Toast.makeText(context, "Can not remove a web contact", Toast.LENGTH_LONG).show();
					} else {
						final Dialog dialog_inner = new Dialog(context);
						dialog_inner.setContentView(R.layout.custom_dialog);
						dialog_inner.setTitle("Confirm Remove Contact   ");
						dialog_inner.getWindow().setTitleColor(Color.parseColor("#008bd0"));
						dialog_inner.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
						Button button1 = (Button) dialog_inner.findViewById(R.id.dialog_btn1);
						button1.setText("           Remove contact          ");
						Button button2= (Button) dialog_inner.findViewById(R.id.dialog_btn2);
						button2.setText("        Cancel        ");


						button1.setVisibility(View.VISIBLE);
						button2.setVisibility(View.VISIBLE);

						button1.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog_inner.dismiss();
								if(new ApplicationUtility(context).checkNetworkConnection()){
									toBeDeleteUserId=friendID;
									removeContactProgressDialog=ProgressDialog.show(context, "", "please wait...");
									try {
										new Thread(){
											@Override
											public void run() {
												serviceConnection=new WebServiceConnection("REMOVE_FRIEND","http://tempuri.org/IiEchoMobileService/RemoveContact", "http://tempuri.org/", "RemoveContact", ApplicationUtility.SERVICE_URL, "customerId", "contactid", ApplicationUtility.USER_ID,friendID );
												removeFriendHandler.sendEmptyMessage(0);
											}
										}.start();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}else{
									Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
								}
							}
						});

						button2.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog_inner.dismiss();
							}
						});
						dialog_inner.show();
					}
				}
			});
			button3.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}else if(requestName.equals("")){
			System.out.println(">>>>>>>friend id is :"+list2.get((int)id));
			contactChoiceDialog((String) list.get(position),list2.get((int)id));
		}
	}

	private void setUpViews() {
		contactListView = (ListView) findViewById(R.id.contacts_list);
		homeButton = (Button) findViewById(R.id.contacts_home_btn);
		addGroupButton= (Button) findViewById(R.id.contacts_add_group_btn);
		searchContactButton=(Button)findViewById(R.id.contacts_search_btn);
	}

	@SuppressWarnings("rawtypes")
	private void loadContactList(){
		/*
		 * get groups
		 */
		ArrayList<ArrayList<String>> arrayList=connectionManager.getAllGroups();
		System.out.println("array list length : "+arrayList.size());
		groupArray=new String[arrayList.size()];
		for (int i = 0; i < arrayList.size(); i++) {
			ArrayList<String> innerArrayList=arrayList.get(i);
			System.out.println(innerArrayList.get(0));
			System.out.println(innerArrayList.get(1));
			System.out.println(innerArrayList.get(2));
			groupArray[i]=innerArrayList.get(1);
		}

		adapter = new SeparatedListAdapter(this);

		//		idArray=connectionManager.getContactId();
		list=new ArrayList();
		list2=new ArrayList<String>();
		for (String element : groupArray) {

			String[] contactNamesArray=connectionManager.getAllContacts(element);
			String[] ss=new String[contactNamesArray.length];
			list.add(element);
			list2.add("");
			System.out.println(element);
			ArrayList test=new ArrayList();
			ArrayList IDList=new ArrayList();
			for (int j = 0; j < contactNamesArray.length; j++) {
				String x=contactNamesArray[j];
				int p=x.indexOf("*");
				x=x.substring(0, p);
				System.out.println(">>>>>> "+x);
				ss[j]=x;
				list.add(x);
				//********
				x=contactNamesArray[j];
				x=x.substring(p+1, x.length());
				list2.add(x);

				String data=connectionManager.getContactType(x);
				System.out.println("data found is ==========> "+x+" "+data);
				totalContacts=list2.size();
				test.add(data);
				IDList.add(x);
				//				ss[j]=list.get(j+1).toString()+" ("+data+")";
			}
			String[] testing=new String[test.size()];
			for (int j = 0; j < testing.length; j++) {
				String value=test.get(j).toString();
				if (value.equalsIgnoreCase("Public")) {
					testing[j]=ss[j]+" ("+test.get(j).toString()+")";	
				} else {
					testing[j]=ss[j];
				}

			}
			//**************
			//			String[] imageStrArr=new String[IDList.size()];
			ArrayList<Bitmap> friendImagesArrayList = new ArrayList<Bitmap>();
			for (int j = 0; j < IDList.size(); j++) {
				System.out.println("Fetching image for user id : "+IDList.get(j));
				Bitmap contactImageArray=connectionManager.getImage(IDList.get(j).toString());
				System.out.println("Adding image =============== "+contactImageArray);
				friendImagesArrayList.add(contactImageArray);
			}
			//**************
			System.out.println("length of ss is :"+ss.length);
			System.out.println("length of testing is : "+testing.length);
			System.out.println("length of friendImagesArrayList is : "+friendImagesArrayList.size());

			listViewCustomAdapter = new ListViewCustomAdapter(ContactsActivity.this, testing, friendImagesArrayList);
			//			ArrayAdapter<String> listadapter = new ArrayAdapter<String>(this,R.layout.list_item, testing);
			adapter.addSection(element, listViewCustomAdapter);
		}
		System.out.println(">>>>>>>>>>>>>>>>length is>>>>>>>>>"+list.size());
		contactListView.setAdapter(adapter);

		contactListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long duration) {
				showChoiceDialog(position);
			}
		});
	}
	

	private void contactChoiceDialog(String name,final String friendId){

		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.custom_dialog);
		dialog.setTitle(name+"");
		dialog.getWindow().setTitleColor(Color.parseColor("#008bd0"));
		dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
		Button button1 = (Button) dialog.findViewById(R.id.dialog_btn1);
		button1.setText("           Remove contact          ");
		Button button2= (Button) dialog.findViewById(R.id.dialog_btn2);
		button2.setText("        Change contact type        ");
		Button button3= (Button) dialog.findViewById(R.id.dialog_btn3);
		button3.setText("        Change group        ");
		Button button4= (Button) dialog.findViewById(R.id.dialog_btn4);
		button4.setText("         Cancel         ");


		button1.setVisibility(View.VISIBLE);
		button2.setVisibility(View.VISIBLE);
		button3.setVisibility(View.VISIBLE);
		button4.setVisibility(View.VISIBLE);


		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (friendId.equals("")) {
					Toast.makeText(context, "Can not remove a web contact", Toast.LENGTH_LONG).show();
				} else {
					final Dialog dialog_inner = new Dialog(context);
					dialog_inner.setContentView(R.layout.custom_dialog);
					dialog_inner.setTitle("Confirm Remove Contact   ");
					dialog_inner.getWindow().setTitleColor(Color.parseColor("#008bd0"));
					dialog_inner.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
					Button button1 = (Button) dialog_inner.findViewById(R.id.dialog_btn1);
					button1.setText("           Remove contact          ");
					Button button2= (Button) dialog_inner.findViewById(R.id.dialog_btn2);
					button2.setText("        Cancel        ");


					button1.setVisibility(View.VISIBLE);
					button2.setVisibility(View.VISIBLE);

					button1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog_inner.dismiss();
							if(new ApplicationUtility(context).checkNetworkConnection()){
								toBeDeleteUserId=friendId;
								removeContactProgressDialog=ProgressDialog.show(context, "", "please wait...");
								try {
									new Thread(){
										@Override
										public void run() {
											serviceConnection=new WebServiceConnection("REMOVE_FRIEND","http://tempuri.org/IiEchoMobileService/RemoveContact", "http://tempuri.org/", "RemoveContact", ApplicationUtility.SERVICE_URL, "customerId", "contactid", ApplicationUtility.USER_ID,friendId );
											removeFriendHandler.sendEmptyMessage(0);
										}
									}.start();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}else{
								Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
							}
						}
					});

					button2.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog_inner.dismiss();
						}
					});
					dialog_inner.show();
				}
			}
		});

		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (friendId.equals("")) {
					Toast.makeText(context, "Can not change state of web contact", Toast.LENGTH_LONG).show();
				} else {
					final Dialog dialog_inn = new Dialog(context);
					dialog_inn.setContentView(R.layout.custom_dialog);
					dialog_inn.setTitle("Maintain friend as         ");
					dialog_inn.getWindow().setTitleColor(Color.parseColor("#008bd0"));
					dialog_inn.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
					Button button1 = (Button) dialog_inn.findViewById(R.id.dialog_btn1);
					button1.setText("Private Contact");
					Button button2= (Button) dialog_inn.findViewById(R.id.dialog_btn2);
					button2.setText("Public Contact");
					Button button3= (Button) dialog_inn.findViewById(R.id.dialog_btn3);
					button3.setText("Cancel");

					button1.setVisibility(View.VISIBLE);
					button2.setVisibility(View.VISIBLE);
					button3.setVisibility(View.VISIBLE);

					friendIdUpdateGroup=friendId;

					button1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if(new ApplicationUtility(context).checkNetworkConnection()){
								try{
									dialog_inn.dismiss();
									progressDialog=ProgressDialog.show(context, "", "please wait...");
									connectionManager.updateContactType(friendIdUpdateGroup, "Private");
									System.out.println(">>>>>>>>>>>>>-----Contact ID--------"+friendIdUpdateGroup);
									new Thread(){
										@Override
										public void run() {
											serviceConnection=new WebServiceConnection("UPDATE_FRIEND_STATE","http://tempuri.org/IiEchoMobileService/UpdateFriendState", "http://tempuri.org/", "UpdateFriendState", ApplicationUtility.SERVICE_URL, "customerId", "friendId", "state", ApplicationUtility.USER_ID, friendIdUpdateGroup, "0");

											if(ApplicationUtility.USER_CURRENT_STATE.equals("Public")){
												changeStateServiceConnection=new WebServiceConnection("RESET_REQUESTS","http://tempuri.org/IiEchoMobileService/UpdateIncommingFriendRequest", "http://tempuri.org/", "UpdateIncommingFriendRequest", ApplicationUtility.SERVICE_URL, "customerId", "customerState", ApplicationUtility.USER_ID,"2");						
											}else if(ApplicationUtility.USER_CURRENT_STATE.equals("Private")){
												changeStateServiceConnection=new WebServiceConnection("RESET_REQUESTS","http://tempuri.org/IiEchoMobileService/UpdateIncommingFriendRequest", "http://tempuri.org/", "UpdateIncommingFriendRequest", ApplicationUtility.SERVICE_URL, "customerId", "customerState", ApplicationUtility.USER_ID,"0");
											}
											updateFriendStateHandler.sendEmptyMessage(0);
										}
									}.start();
								}catch (Exception e) {
									e.printStackTrace();
								}
							}else{
								Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
							}
						}
					});
					button2.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if(new ApplicationUtility(context).checkNetworkConnection()){
								try{
									dialog_inn.dismiss();
									progressDialog=ProgressDialog.show(context, "", "please wait...");
									connectionManager.updateContactType(friendIdUpdateGroup, "Public");
									new Thread(){
										@Override
										public void run() {
											serviceConnection=new WebServiceConnection("UPDATE_FRIEND_STATE","http://tempuri.org/IiEchoMobileService/UpdateFriendState", "http://tempuri.org/", "UpdateFriendState", ApplicationUtility.SERVICE_URL, "customerId", "friendId", "state", ApplicationUtility.USER_ID, friendIdUpdateGroup, "1");
											updateFriendStateHandler.sendEmptyMessage(0);
										}
									}.start();
									//								friendIdUpdateGroup="";
								}catch (Exception e) {
									e.printStackTrace();
								}
							}else{
								Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
							}
						}
					});
					button3.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog_inn.dismiss();
						}
					});

					dialog_inn.show();
				}
			}
		});
		button3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (friendId.equals("")) {
					Toast.makeText(context, "Can not change default group of web contact", Toast.LENGTH_LONG).show();
				}else{
					friendIdUpdateGroup=friendId;
					Intent groupChooserIntent=new Intent(context, GroupChooserActivity.class);
					//					dialog.dismiss();
					startActivityForResult(groupChooserIntent, 605);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(requestName.equals("")){
			MenuItem menuItem=menu.add(1, 1, 0, "Refresh List");
			menuItem.setIcon(R.drawable.icon_refresh);
			MenuItem menuItem2=menu.add(1, 2, 0, "Remove Group");
			menuItem2.setIcon(R.drawable.icon_delete);
			MenuItem menuItem3=menu.add(1, 3, 0, "Add Friend");
			menuItem3.setIcon(R.drawable.add_contect);
		}else{
			System.out.println("request from :"+requestName);
			MenuItem menuItem3=menu.add(1, 3, 0, "Add Friend");
			menuItem3.setIcon(R.drawable.add_contect);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId()==1) {
			if(new ApplicationUtility(context).checkNetworkConnection()){
				progressDialog=ProgressDialog.show(context, "", "please wait...");
				new Thread(){
					@Override
					public void run() {
						//updateWebContacts();
						refreshContactList();
						updateContactList();

						refreshContectListHandler.sendEmptyMessage(0);
					};
				}.start();
			}else{
				Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
			}
		}else if(item.getItemId()==2){

			ArrayList<ArrayList<String>> arrayList = connectionManager.getAllGroups();
			System.out.println("array list length : " + arrayList.size());
			if(arrayList.size()==1){
				Toast.makeText(context, "You can't delete the default group", Toast.LENGTH_LONG).show();
			}else{
				Intent groupIntent=new Intent(context, GroupChooserActivity.class);
				groupIntent.putExtra("group_to_delete", "yes");
				startActivityForResult(groupIntent, 115);
			}

		}else if (item.getItemId() == 3){
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
			button3.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();

		}
		return true;
	}

	private Handler requestHandler;


	private void updateContactList(){
		String[] dbUIDArray=connectionManager.getAllUsersId();
		for (int i = 0; i < dbUIDArray.length; i++) {
			long l=connectionManager.updatePhoneNumber(contactNumber[i],fName[i],lName[i],usersId[i],imageArrayList.get(i));
			System.out.println("number updated successfully -------"+l);
		}
	}

	private void refreshContactList(){
		try{
			checkUpdateFriendContacts();
			for (int i = 0; i < usersId.length; i++) {
				String con_name=connectionManager.checkForIdExists(usersId[i]);
				System.out.println("name is: "+con_name);
				if(con_name.equals("")){
					try{
						long rowid=connectionManager.addNewContact(new ContactVO(fName[i], lName[i], contactNumber[i], emailId[i], "Public", "My Group", "0", "1", usersId[i],imageArrayList.get(i)));
						System.out.println("contact addad at>>>>>>"+rowid);
					}catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					System.out.println("No need to update>>>>>>");
				}
			}
			checkForContactDelete();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateWebContacts(){
		try {
			connectionManager.deleteWebContects();
			new Thread(){
				@Override
				public void run() {
					webContectServiceConnection=new WebServiceConnection("GET_SERVER_CONTACT_LIST", "http://tempuri.org/IiEchoMobileService/GetWebSiteContactList", "http://tempuri.org/", "GetWebSiteContactList", ApplicationUtility.SERVICE_URL,"customerId",ApplicationUtility.USER_ID);
					updateWebContactsHandler.sendEmptyMessage(0);
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void afterResponse(){
		try {
			webContectJsonObject=webContectServiceConnection.jsonResponse();

			if (webContectJsonObject!=null) {

				JSONArray aryJSONStrings = new JSONArray(webContectJsonObject.getString("ContactList"));

				for (int i = 0; i < aryJSONStrings.length(); i++) {
					JSONObject jsonObject = aryJSONStrings.getJSONObject(i);
					try{
						Log.i("ManageFriendsId >>>>> ", jsonObject.getString("FriendId"));
						user_id=jsonObject.getString("FriendId");
					} catch (Exception e) {

					}try{
						Log.i("FirstName >>>>> ", jsonObject.getString("FirstName"));
						f_name=jsonObject.getString("FirstName");
					} catch (Exception e) {

					}try{
						Log.i("LastName >>>>> ", jsonObject.getString("LastName"));
						l_name=jsonObject.getString("LastName");
					} catch (Exception e) {

					}try{
						Log.i("MobileNumber >>>>> ", jsonObject.getString("MobileNumber"));
						number=jsonObject.getString("MobileNumber");
					} catch (Exception e) {

					}try{
						Log.i("ImageURL >>>>> ", jsonObject.getString("ImageURL"));
						image_url = jsonObject.getString("ImageURL");
					} catch (Exception e) {

					}
					if(!image_url.equals("")){
						fetchUserImage(image_url);
					}
					connectionManager.addNewContact(new ContactVO(f_name, l_name, number, "","", "My Group", "1", "1", "",friendImageByteArray));
					friendImageByteArray = null;
				}
			}

		} catch (Exception e) {

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
				System.out.println("====>>>>>>>>>"+flag);
				if(flag==false){
					toDeleteUID=element;
					System.out.println(element);
					long row_id=connectionManager.deleteContact(Long.parseLong(toDeleteUID));
					System.out.println("contact deleted at>>>>>> "+row_id);
				}
				flag=false;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}



	private void checkUpdateFriendContacts(){
		try {
			serviceConnection=new WebServiceConnection("GET_ALL_FRIENDS_LIST", "http://tempuri.org/IiEchoMobileService/GetFriendList", "http://tempuri.org/", "GetFriendList", ApplicationUtility.SERVICE_URL,"customerId",ApplicationUtility.USER_ID);
			responseJsonObject=serviceConnection.jsonResponse();
			System.out.println("responseJsonObject>>>>>>>>>>> "+responseJsonObject);
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
						email_=jsonObject.getString("EmailId");
						emailId[i]=email_;
					} catch (Exception e) {

					}try{
						Log.i("FriendStatus >>>>> ", jsonObject.getString("FriendStatus"));
						type=jsonObject.getString("FriendStatus");
					} catch (Exception e) {

					}try{
						Log.i("ImageURL >>>>> ", jsonObject.getString("ImageURL"));
						image_url = jsonObject.getString("ImageURL");
					} catch (Exception e) {
						e.printStackTrace();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==1024){
			Bundle bundle=data.getExtras();
			String group_name=bundle.getString("group_name");
			connectionManager.updateContactGroup(friendIdUpdateGroup, group_name);
			friendIdUpdateGroup="";
			try {
				loadContactList();	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(resultCode==1028){
			Bundle bundle=data.getExtras();
			final String group_name=bundle.getString("group_name");
			if (group_name.equals("My Group")) {
				Toast.makeText(context, "You can not delete default group", Toast.LENGTH_LONG).show();	
			} else {
				final Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.custom_dialog);
				dialog.setTitle("Delete Group Confirm           ");
				dialog.getWindow().setTitleColor(Color.parseColor("#008bd0"));
				dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
				Button button1 = (Button) dialog.findViewById(R.id.dialog_btn1);
				button1.setText("Delete Group");
				Button button2= (Button) dialog.findViewById(R.id.dialog_btn2);
				button2.setText("Cancel");

				button1.setVisibility(View.VISIBLE);
				button2.setVisibility(View.VISIBLE);

				button1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(new ApplicationUtility(context).checkNetworkConnection()){
							dialog.dismiss();
							progressDialog=ProgressDialog.show(context, "", "please wait...");
							new Thread(){
								@Override
								public void run() {
									System.out.println("group name to delete>>>>>>>>>>>>>>>>>"+group_name);
									connectionManager.deleteGroup(group_name);
									connectionManager.updateContectGroup(group_name);
									refreshContectListHandler.sendEmptyMessage(0);
									comfirmDeleteHandler.sendEmptyMessage(0);
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
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		} else if (resultCode == 418) {
			finish();
		} else if (resultCode == 8888) {
			try {
				loadContactList();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		arrayLists = null;
		firstName = null;
		lastName = null;
		email = null;
		phoneNumber = null;
		userId = null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		connectionManager.closeDB();
	}


	//****************************** inner adapter class starts ******************************************

	class ListViewCustomAdapter extends BaseAdapter {
		public String title[];
		public ArrayList<Bitmap> description;
		public Activity context;
		public LayoutInflater inflater;

		public ListViewCustomAdapter(Activity context, String[] title,ArrayList<Bitmap> image) {
			super();
			this.context = context;
			this.title = title;
			this.description = image;
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return title.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		class ViewHolder {
			ImageView imgViewLogo;
			ImageView arrowImgViewLogo;
			TextView txtViewTitle;
			RelativeLayout relativeLayout;
		}

		ViewHolder holder;
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.contacts_custom_row, null);
				holder.imgViewLogo = (ImageView) convertView.findViewById(R.id.imgViewLogo);
				holder.txtViewTitle = (TextView) convertView.findViewById(R.id.txtViewTitle);
				holder.arrowImgViewLogo = (ImageView) convertView.findViewById(R.id.imgViewLogo_arrow);
				holder.relativeLayout = (RelativeLayout)convertView.findViewById(R.id.relativeLayout1);
				convertView.setTag(holder);
			} else
			holder = (ViewHolder) convertView.getTag();
			holder.txtViewTitle.setText(title[position]);
			holder.imgViewLogo.setImageBitmap(description.get(position));
			
			
			holder.imgViewLogo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});
			holder.txtViewTitle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});
			return convertView;
		}
	}
	//****************************** inner adapter class ends ******************************************
}