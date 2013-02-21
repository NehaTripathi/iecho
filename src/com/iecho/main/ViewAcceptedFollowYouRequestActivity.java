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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.services.SendUpdatedLocationService;
import com.iecho.webservice.WebServiceConnection;

public class ViewAcceptedFollowYouRequestActivity extends Activity{
	private Button homeButton;
	private TextView requestNumberTextView;
	private ListView detailListView;
	private Context context;
	private ListViewCustomAdapter adapter;
	private ProgressDialog progressDialog;
	private Handler listHandler;
	private WebServiceConnection serviceConnection;
	private JSONObject responseJsonObject;
	private JSONArray aryJSONStrings;
	private String[] fName,lName,phoneNumber,email,userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_accepted_follow_you_request);
		context=this;
		setUpViews();

		Button infoButton=(Button)findViewById(R.id.view_accepted_follow_you_info);
		infoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent infoIntent=new Intent(context, AboutIechoActivity.class);
				startActivity(infoIntent);
			}
		});

		progressDialog=ProgressDialog.show(context, "", "please wait...");

		listHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(progressDialog!=null){
					progressDialog.dismiss();
				}
				try {
					afterResponse();
					if(aryJSONStrings!=null){
						requestNumberTextView.setText(aryJSONStrings.length()+" Accepted Request");
					}
					if(responseJsonObject!=null){
						adapter=new ListViewCustomAdapter(ViewAcceptedFollowYouRequestActivity.this, fName, lName);
						detailListView.setAdapter(adapter);
					}else{
						System.out.println("responseJsonObject is null >>>> ");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		new Thread(){
			public void run() {
				serviceConnection=new WebServiceConnection("GET_ACCEPTED_FOLLOW_YOU_REQUESTS", "http://tempuri.org/IiEchoMobileService/GetAcceptedFollowYouRequest", "http://tempuri.org/", "GetAcceptedFollowYouRequest", ApplicationUtility.SERVICE_URL,"customerId",ApplicationUtility.USER_ID);
				listHandler.sendEmptyMessage(0);
			};
		}.start();

		homeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(1290);
				finish();
			}
		});

	}

	private void setUpViews(){
		homeButton=(Button)findViewById(R.id.view_accepted_follow_you_req_home_btn);
		requestNumberTextView=(TextView)findViewById(R.id.view_accepted_follow_you_req_number_text);
		detailListView=(ListView)findViewById(R.id.view_accepted_follow_you_req_list);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem menuItem=menu.add(1, 1, 0, "Refresh List");
		menuItem.setIcon(R.drawable.icon_refresh);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(new ApplicationUtility(context).checkNetworkConnection()){
			if (item.getItemId()==1) {
				progressDialog=ProgressDialog.show(context, "", "please wait...");

				new Thread(){
					public void run() {
						serviceConnection=new WebServiceConnection("GET_ACCEPTED_FOLLOW_YOU_REQUESTS", "http://tempuri.org/IiEchoMobileService/GetAcceptedFollowYouRequest", "http://tempuri.org/", "GetAcceptedFollowYouRequest", ApplicationUtility.SERVICE_URL,"customerId",ApplicationUtility.USER_ID);
						listHandler.sendEmptyMessage(0);
					};
				}.start();
			}
		}else{
			Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
		}
		return true;
	}

	private void afterResponse(){
		try {
			responseJsonObject=serviceConnection.jsonResponse();

			if (responseJsonObject!=null) {

				aryJSONStrings = new JSONArray(responseJsonObject.getString("AcceptedFriendList"));
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

				holder.imgViewAccept.setVisibility(View.INVISIBLE);
				holder.imgViewReject.setVisibility(View.INVISIBLE);

				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();

			holder.imgViewDetail.setImageResource(R.drawable.btn_arrow);
			holder.imgViewAccept.setImageResource(R.drawable.btn_plus);
			holder.imgViewReject.setImageResource(R.drawable.btn_cross);
			holder.txtViewTitle.setText(title[position]+" "+description[position]);

			holder.txtViewTitle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});
			holder.imgViewDetail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(new ApplicationUtility(context).checkNetworkConnection()){
						Intent friendLocationMapIntent=new Intent(context, FriendLocationMapActivity.class);
						friendLocationMapIntent.putExtra("friendId", userId[position]);
						friendLocationMapIntent.putExtra("fisrtName", fName[position]);
						friendLocationMapIntent.putExtra("lastName", lName[position]);
						startActivityForResult(friendLocationMapIntent, 950);
						//					serviceConnection=new WebServiceConnection("GET_FRIEND_UPDATED_LOCATION", "http://tempuri.org/IiEchoMobileService/GetFindMeTrackingInformation", "http://tempuri.org/", "GetFindMeTrackingInformation", ApplicationUtility.SERVICE_URL,"customerId",userId[position]);
					}else{
						Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
					}
				}
			});
			holder.imgViewReject.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});
			holder.imgViewAccept.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

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
