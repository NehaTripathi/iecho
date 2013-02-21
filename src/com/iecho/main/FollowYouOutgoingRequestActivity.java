package com.iecho.main;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.services.SendUpdatedLocationService;
import com.iecho.webservice.WebServiceConnection;

public class FollowYouOutgoingRequestActivity extends Activity {
	private ListView detailListView;
	private Context context;
	private ProgressDialog progressDialog;
	private Handler listHandler;
	private WebServiceConnection serviceConnection;
	private JSONObject responseJsonObject;
	private JSONArray aryJSONStrings;
	private String[] fName,lName,phoneNumber,email,userId;
	private TextView pendingTextView;
	private ListViewCustomAdapter adapter;
	private String firstName,lastName,contactName,userEmail,userid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.follow_you_outgoing_req);
		context=this;
		setUpViews();
		Button infoButton=(Button)findViewById(R.id.follow_you_out_info);
		infoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent infoIntent=new Intent(context, AboutIechoActivity.class);
				startActivity(infoIntent);
			}
		});
		progressDialog=ProgressDialog.show(context, "", "please wait...");
		try {
			new Thread(){
				public void run() {
					serviceConnection=new WebServiceConnection("FOLLOWYOU_OUTGOING_PENDING_REQUEST", "http://tempuri.org/IiEchoMobileService/GetPendingFollowYouRequestOutGoing", "http://tempuri.org/", "GetPendingFollowYouRequestOutGoing", ApplicationUtility.SERVICE_URL, "customerId",  ApplicationUtility.USER_ID);
					listHandler.sendEmptyMessage(0);
				};
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		listHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(progressDialog!=null){
					progressDialog.dismiss();
				}
				try {
					afterResponse();
					pendingTextView.setText(aryJSONStrings.length()+" Pending Request");
					adapter=new ListViewCustomAdapter(FollowYouOutgoingRequestActivity.this, fName, lName);
					detailListView.setAdapter(adapter);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

	}

	private void setUpViews(){
		pendingTextView=(TextView)findViewById(R.id.follow_you_out_req_number_text);
		detailListView=(ListView)findViewById(R.id.follow_you_out_req_list);
	}


	private void afterResponse(){
		try {
			responseJsonObject=serviceConnection.jsonResponse();

			if (responseJsonObject!=null) {

				aryJSONStrings = new JSONArray(responseJsonObject.getString("ContactList"));
				fName=new String[aryJSONStrings.length()];
				lName=new String[aryJSONStrings.length()];
				phoneNumber=new String[aryJSONStrings.length()];
				email=new String[aryJSONStrings.length()];
				userId=new String[aryJSONStrings.length()];
				for (int i = 0; i < aryJSONStrings.length(); i++) {
					JSONObject jsonObject = aryJSONStrings.getJSONObject(i);
					try{
						Log.i("ManageFriendsId >>>>> ", jsonObject.getString("CustomerId"));
						userId[i]=jsonObject.getString("CustomerId");
					} catch (Exception e) {

					}try{
						Log.i("FirstName >>>>> ", jsonObject.getString("FirstName"));
						fName[i]=jsonObject.getString("FirstName");
					} catch (Exception e) {

					}try{
						Log.i("LastName >>>>> ", jsonObject.getString("LastName"));
						lName[i]=jsonObject.getString("LastName");
					} catch (Exception e) {

					}try{
						Log.i("MobileNumber >>>>> ", jsonObject.getString("Phone"));
						phoneNumber[i]=jsonObject.getString("Phone");
					} catch (Exception e) {

					}try{
						Log.i("EmailId >>>>> ", jsonObject.getString("EmailId"));
						email[i]=jsonObject.getString("EmailId");
					} catch (Exception e) {

					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	class ListViewCustomAdapter extends BaseAdapter {
		public String title[];
		public String description[];
		public Activity context;
		public LayoutInflater inflater;

		public ListViewCustomAdapter(Activity context, String[] title,String[] description) {
			super();

			this.context = context;
			this.title = title;
			this.description = description;

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

		public class ViewHolder {
			ImageView imgViewDetail,imgViewReject,imgViewAccept;
			TextView txtViewTitle;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {


			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.custom_row, null);
				holder.imgViewDetail = (ImageView) convertView.findViewById(R.id.imgViewLogo);
				holder.imgViewAccept = (ImageView) convertView.findViewById(R.id.imgViewReject);
				holder.imgViewReject = (ImageView) convertView.findViewById(R.id.imgViewAccept);
				holder.txtViewTitle = (TextView) convertView.findViewById(R.id.txtViewTitle);
				holder.imgViewDetail.setVisibility(View.INVISIBLE);
				holder.imgViewAccept.setVisibility(View.INVISIBLE);
				holder.imgViewReject.setVisibility(View.INVISIBLE);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();

			holder.imgViewDetail.setImageResource(R.drawable.btn_arrow);
			holder.imgViewAccept.setImageResource(R.drawable.btn_plus);
			holder.imgViewReject.setImageResource(R.drawable.btn_cross);
			holder.txtViewTitle.setText(title[position]);

			holder.txtViewTitle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//					Toast.makeText(context, title[position], 3000).show();
				}
			});
			holder.imgViewDetail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//					Toast.makeText(context, title[position], 3000).show();
					Intent friendDetailIntent=new Intent(context,ViewFriendProfileActivity.class);
					friendDetailIntent.putExtra("fName", fName[position]);
					friendDetailIntent.putExtra("lName", lName[position]);
					friendDetailIntent.putExtra("number", phoneNumber[position]);
					friendDetailIntent.putExtra("email", email[position]);
					friendDetailIntent.putExtra("userId", userId[position]);
					context.startActivityForResult(friendDetailIntent, 555);
				}
			});
			holder.imgViewReject.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					progressDialog=ProgressDialog.show(context, "", "please wait...");
					new Thread(){
						public void run() {
							serviceConnection=new WebServiceConnection("DECLINE_FOLLOW_YOU_INCOMING_REQUEST", "http://tempuri.org/IiEchoMobileService/DeclineFollowMeRequest", "http://tempuri.org/", "DeclineFollowMeRequest", ApplicationUtility.SERVICE_URL,"customerId","requestId",ApplicationUtility.USER_ID,userId[position]);
							serviceConnection=new WebServiceConnection("FOLLOWYOU_OUTGOING_PENDING_REQUEST", "http://tempuri.org/IiEchoMobileService/GetPendingFollowYouRequestIncomming", "http://tempuri.org/", "GetPendingFollowYouRequestIncomming", ApplicationUtility.SERVICE_URL, "customerId",  ApplicationUtility.USER_ID);
							listHandler.sendEmptyMessage(0);
						};
					}.start();
				}
			});
			holder.imgViewAccept.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					progressDialog=ProgressDialog.show(context, "", "please wait...");
					new Thread(){
						public void run() {
							serviceConnection=new WebServiceConnection("ACCEPT_FOLLOW_YOU_INCOMING_REQUEST", "http://tempuri.org/IiEchoMobileService/AcceptFollowMeRequest", "http://tempuri.org/", "AcceptFollowMeRequest", ApplicationUtility.SERVICE_URL,"customerId","requestId",ApplicationUtility.USER_ID,userId[position]);
							serviceConnection=new WebServiceConnection("FOLLOWYOU_INCOMING_PENDING_REQUEST", "http://tempuri.org/IiEchoMobileService/GetPendingFollowYouRequestIncomming", "http://tempuri.org/", "GetPendingFollowYouRequestIncomming", ApplicationUtility.SERVICE_URL, "customerId",  ApplicationUtility.USER_ID);
							listHandler.sendEmptyMessage(0);
						};
					}.start();
				}
			});

			return convertView;
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
