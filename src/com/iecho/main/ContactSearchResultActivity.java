package com.iecho.main;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.database.DataBaseConnectionManager;
import com.iecho.main.ContactsActivity.ListViewCustomAdapter.ViewHolder;
import com.iecho.webservice.WebServiceConnection;

public class ContactSearchResultActivity extends Activity implements OnItemClickListener{
	private Button homeButton;
	private TextView requestNumberTextView;
	private ListView detailListView;
	private Context context;
	//	private ContactResultListViewAdapter adapter;
	//	private ArrayAdapter<String> arrayAdapter;
	private String firstName[],lastName[],email[],phoneNumber[],userId[];
	private Bitmap[] imageBitmaps;
	private ProgressDialog progressDialog;
	private Handler handler,removeFriendHandler,updateFriendStateHandler;
	private String requestName="";
	private WebServiceConnection serviceConnection,changeStateServiceConnection;
	private HashMap<String, String> serviceResponce;
	private String destination="",time="",startPoint="";
	private DataBaseConnectionManager connectionManager;
	private String toBeDeleteUserId="";
	private ListViewCustomAdapter listViewCustomAdapter;
	private Bitmap[] imagesBitmaps;
	private ProgressDialog removeContactProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_search_result);
		context=this;
		setUpViews();
		connectionManager=new DataBaseConnectionManager(context);
		Button infoButton=(Button)findViewById(R.id.contact_search_info);
		infoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent infoIntent=new Intent(context, AboutIechoActivity.class);
				startActivity(infoIntent);
			}
		});
		try{
			Intent intent=getIntent();
			Bundle bundle=intent.getExtras();
			firstName=	bundle.getStringArray("fNameArray");
			lastName=bundle.getStringArray("lNameArray");
			phoneNumber=bundle.getStringArray("numberArray");
			email=bundle.getStringArray("emailArray");
			userId=bundle.getStringArray("userIdArray");
			requestName=bundle.getString("requestName");
			//			imagesBitmaps=(Bitmap[])bundle.getParcelableArray("userImageArray");
			if (requestName.equals("SendNotification")) {
				startPoint=bundle.getString("startPoint");
				destination=bundle.getString("destination");
				time=bundle.getString("time");
			}
			System.out.println(firstName.length);
			System.out.println(lastName.length);
			System.out.println(phoneNumber.length);
			System.out.println(email.length);
			System.out.println(userId.length);
			imagesBitmaps = new Bitmap[userId.length];
			for (int i = 0; i < userId.length; i++) {
				try{
					imagesBitmaps[i]=connectionManager.getImage(userId[i]);
				}catch (Exception e) {
					e.printStackTrace();
					imagesBitmaps[i] = null;
				}
			}
			requestNumberTextView.setText(""+firstName.length+" Result Found");
			//			arrayAdapter=new ArrayAdapter<String>(context, R.layout.spinnerlayout, firstName);
			//			arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			listViewCustomAdapter=new ListViewCustomAdapter(ContactSearchResultActivity.this, firstName, imagesBitmaps);
			detailListView.setAdapter(listViewCustomAdapter);
		}catch (Exception e) {
			e.printStackTrace();
		}
		detailListView.setOnItemClickListener(this);
		//		adapter = new ContactResultListViewAdapter(this, firstName, lastName);
		//		detailListView.setAdapter(adapter);

		homeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				setResult(418);
				finish();
			}
		});

		handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(progressDialog!=null){
					progressDialog.dismiss();
				}

				try {
					serviceResponce=serviceConnection.serviceResponce();
					if(serviceResponce!=null){
						String status=serviceResponce.get("status");
						String message=serviceResponce.get("statusMsg");
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
				setResult(418);
				finish();
			}
		};

		/**
		 * Handler called after removal of contact 
		 */
		removeFriendHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(progressDialog!=null){
					progressDialog.dismiss();
				}
				try {
					Toast.makeText(context, "Contact deleted", Toast.LENGTH_SHORT).show();
					connectionManager.deleteContact(Long.parseLong(toBeDeleteUserId));
					toBeDeleteUserId="";
					setResult(8888);
					if(removeContactProgressDialog!=null){
						removeContactProgressDialog.dismiss();
					}
					Toast.makeText(context, "Contact removed successfully", Toast.LENGTH_LONG).show();
					finish();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};

		/**
		 * Handler for updating state of friend
		 */
		updateFriendStateHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(progressDialog!=null){
					progressDialog.dismiss();
				}
				setResult(8888);
				finish();
				Toast.makeText(context, "Contact type changed successfully", Toast.LENGTH_LONG).show();
			}
		};

	}

	private void setUpViews(){
		homeButton=(Button)findViewById(R.id.contact_result_home_btn);
		requestNumberTextView=(TextView)findViewById(R.id.contact_result_number_text);
		detailListView=(ListView)findViewById(R.id.contact_result_list);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		//Toast.makeText(this,"Title => " + firstName[arg2] + " n Description => "+ lastName[arg2]+" Id >"+userId[arg2], Toast.LENGTH_SHORT).show();
		System.out.println("Sending request for user id >>>>>>>>>>> "+userId[arg2]);
		if (requestName.equals("")) {
			contactChoiceDialog(firstName[arg2], userId[arg2]);
		} else {
			final int pos=arg2;
			final Dialog dialog = new Dialog(context);
			dialog.setContentView(R.layout.custom_dialog);
			dialog.setTitle("Send Request Confirm             ");
			dialog.getWindow().setTitleColor(Color.parseColor("#008bd0"));
			dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
			Button button1 = (Button) dialog.findViewById(R.id.dialog_btn1);
			button1.setText("Send Request");
			Button button2= (Button) dialog.findViewById(R.id.dialog_btn2);
			button2.setText("Remove Contact");
			Button button3 = (Button) dialog.findViewById(R.id.dialog_btn3);
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
					dialog.dismiss();
					sendRequest(userId[pos]);
				}
			});
			button2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					if (userId[pos].equals("")) {
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
									toBeDeleteUserId=userId[pos];
									removeContactProgressDialog=ProgressDialog.show(context, "", "please wait...");
									try {
										new Thread(){
											@Override
											public void run() {
												serviceConnection=new WebServiceConnection("REMOVE_FRIEND","http://tempuri.org/IiEchoMobileService/RemoveContact", "http://tempuri.org/", "RemoveContact", ApplicationUtility.SERVICE_URL, "customerId", "contactid", ApplicationUtility.USER_ID,userId[pos] );
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
		}
	}

	private void contactChoiceDialog(String name,final String friendid){

		toBeDeleteUserId=friendid;

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
				if (friendid.equals("")) {
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

								progressDialog=ProgressDialog.show(context, "", "please wait...");
								try {
									new Thread(){
										@Override
										public void run() {
											serviceConnection=new WebServiceConnection("REMOVE_FRIEND","http://tempuri.org/IiEchoMobileService/RemoveContact", "http://tempuri.org/", "RemoveContact", ApplicationUtility.SERVICE_URL, "customerId", "contactid", ApplicationUtility.USER_ID,friendid );
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
				if (friendid.equals("")) {
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

					connectionManager=new DataBaseConnectionManager(context);

					button1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if(new ApplicationUtility(context).checkNetworkConnection()){
								try{
									dialog_inn.dismiss();
									progressDialog=ProgressDialog.show(context, "", "please wait...");
									connectionManager.updateContactType(friendid, "Private");

									new Thread(){
										@Override
										public void run() {
											serviceConnection=new WebServiceConnection("UPDATE_FRIEND_STATE","http://tempuri.org/IiEchoMobileService/UpdateFriendState", "http://tempuri.org/", "UpdateFriendState", ApplicationUtility.SERVICE_URL, "customerId", "friendId", "state", ApplicationUtility.USER_ID, friendid, "0");
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
									connectionManager.updateContactType(friendid, "Public");
									new Thread(){
										@Override
										public void run() {
											serviceConnection=new WebServiceConnection("UPDATE_FRIEND_STATE","http://tempuri.org/IiEchoMobileService/UpdateFriendState", "http://tempuri.org/", "UpdateFriendState", ApplicationUtility.SERVICE_URL, "customerId", "friendId", "state", ApplicationUtility.USER_ID, friendid, "1");
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
				if (friendid.equals("")) {
					Toast.makeText(context, "Can not change default group of web contact", Toast.LENGTH_LONG).show();
				}else{
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

	private void sendRequest(final String friendId){
		if(new ApplicationUtility(context).checkNetworkConnection()){
			if (friendId.equals("")) {
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
						try {
							if(requestName.equals("FollowRequest")){
								serviceConnection=new WebServiceConnection("FOLLOW_YOU_REQUEST","http://tempuri.org/IiEchoMobileService/FollowYou", "http://tempuri.org/", "FollowYou", ApplicationUtility.SERVICE_URL, "customerId", "friendId", ApplicationUtility.USER_ID, friendId);
							}else if (requestName.equals("TrackRequest")) {
								serviceConnection=new WebServiceConnection("FIND_ME_REQUEST","http://tempuri.org/IiEchoMobileService/FindMe", "http://tempuri.org/", "FindMe", ApplicationUtility.SERVICE_URL, "customerId", "friendId", ApplicationUtility.USER_ID, friendId);	
							}else if (requestName.equals("SendNotification")) {
								serviceConnection=new WebServiceConnection("SEND_NOTIFICATION","http://tempuri.org/IiEchoMobileService/SendNotification", "http://tempuri.org/", "SendNotification", ApplicationUtility.SERVICE_URL, "userid", "friendid","startPoint","destination","time","CountryCode" ,ApplicationUtility.USER_ID, friendId,startPoint,destination,time,"1");
							}
							handler.sendEmptyMessage(0);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.start();
			}
		}else{
			Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==1024){
			Bundle bundle=data.getExtras();
			String group_name=bundle.getString("group_name");
			connectionManager.updateContactGroup(toBeDeleteUserId, group_name);
			setResult(8888);
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try{
			connectionManager.closeDB();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	//****************************** inner adapter class starts ******************************************

	class ListViewCustomAdapter extends BaseAdapter {
		public String title[];
		public Bitmap[] description;
		public Activity context;
		public LayoutInflater inflater;

		public ListViewCustomAdapter(Activity context, String[] title,Bitmap[] image) {
			super();
			this.context = context;
			this.title = title;
			this.description = image;
			System.out.println("========> description "+description.length);
			System.out.println("========> title "+title.length);
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
			TextView txtViewTitle;
			//			RelativeLayout layout;
			//			TextView nameTextView;
		}

		ViewHolder holder;
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {



			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.contacts_custom_row, null);
				holder.imgViewLogo = (ImageView) convertView.findViewById(R.id.imgViewLogo);
				holder.txtViewTitle = (TextView) convertView.findViewById(R.id.txtViewTitle);
				//				holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
				//				holder.layout = (RelativeLayout) convertView.findViewById(R.id.relativeLayout1);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();


			holder.txtViewTitle.setText(title[position]);

			holder.imgViewLogo.setImageBitmap(description[position]);
			//			holder.nameTextView.setText(description[position]);
			//			try{
			//				setIcon(title[position]);
			//			}catch (Exception e) {
			//				e.printStackTrace();
			//			}

			//			holder.txtViewTitle.setOnClickListener(new OnClickListener() {
			//
			//				@Override
			//				public void onClick(View arg0) {
			//					dialog=ProgressDialog.show(context, "", "loading...");
			//					new Thread(){
			//						public void run() {
			//							receiveNumber(title[position]);							
			//						};
			//					}.start();
			//				}
			//			});
			//			holder.imgViewLogo.setOnClickListener(new OnClickListener() {
			//
			//				@Override
			//				public void onClick(View arg0) {
			//					dialog=ProgressDialog.show(context, "", "loading...");
			//					new Thread(){
			//						public void run() {
			//							receiveNumber(title[position]);							
			//						};
			//					}.start();
			//				}
			//			});
			//			holder.layout.setOnClickListener(new OnClickListener() {
			//
			//				@Override
			//				public void onClick(View arg0) {
			//					//						dialog=ProgressDialog.show(context, "", "loading...");
			//					//						new Thread(){
			//					//							public void run() {
			//					//								receiveNumber(title[position]);							
			//					//							};
			//					//						}.start();
			//				}
			//			});

			//holder.txtViewDescription.setText(description[position]);

			return convertView;
		}

	}

	//****************************** inner adapter class ends ******************************************


}
