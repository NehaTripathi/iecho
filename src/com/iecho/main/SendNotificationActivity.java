package com.iecho.main;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.iecho.database.DataBaseConnectionManager;
import com.iecho.services.SendUpdatedLocationService;

public class SendNotificationActivity extends Activity {
	private ListView detailListView;
	private Context context;
	private ListViewCustomAdapter adapter;
	private DataBaseConnectionManager connectionManager;
	private ArrayList<ArrayList<String>> arrayList;
	private String[] notiName,notiDestination,notiStart,notiTime,notiUserId,rowId;
	private int notificationCount; 
	private TextView notificationNumberTextView;
	private Button homeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_notification);
		context=this;
		setUpViews();

		Button infoButton=(Button)findViewById(R.id.send_noti_in_info);
		infoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent infoIntent=new Intent(context, AboutIechoActivity.class);
				startActivity(infoIntent);
			}
		});

		homeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(1452);
				finish();
			}
		});

		connectionManager=new DataBaseConnectionManager(context);
		getNotifications();
	}

	private void setUpViews(){
		notificationNumberTextView=(TextView)findViewById(R.id.send_noti_inc_req_number_text);
		detailListView=(ListView)findViewById(R.id.send_noti_inc_req_list);
		homeButton=(Button)findViewById(R.id.send_noti_inc_req_home_btn);
	}


	private void getNotifications(){
		arrayList=connectionManager.getNotificationDetails();
		System.out.println("array list size : "+arrayList.size());
		notificationCount=arrayList.size();
		notificationNumberTextView.setText(notificationCount+" Destination");

		notiName=new String[arrayList.size()];
		notiDestination=new String[arrayList.size()];
		notiStart=new String[arrayList.size()];
		notiTime=new String[arrayList.size()];
		notiUserId=new String[arrayList.size()];
		rowId=new String[arrayList.size()];

		for (int i = 0; i < arrayList.size(); i++) {
			ArrayList<String> innerArrayList=arrayList.get(i);
			System.out.println(innerArrayList.get(0));
			System.out.println(innerArrayList.get(1));
			System.out.println(innerArrayList.get(2));
			System.out.println(innerArrayList.get(3));
			System.out.println(innerArrayList.get(4));
			System.out.println(innerArrayList.get(5));

			notiName[i]=innerArrayList.get(0);
			notiDestination[i]=innerArrayList.get(1);
			notiStart[i]=innerArrayList.get(2);
			notiTime[i]=innerArrayList.get(3);
			notiUserId[i]=innerArrayList.get(4);
			rowId[i]=innerArrayList.get(5);
		}
		adapter=new ListViewCustomAdapter(SendNotificationActivity.this, notiName, notiDestination);
		detailListView.setAdapter(adapter);
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

				}
			});
			holder.imgViewReject.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try{
						connectionManager.deleteNotification(Long.parseLong(rowId[position]));
						getNotifications();
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			holder.imgViewAccept.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent sendNotificationIntent=new Intent(context, ContactsActivity.class);
					sendNotificationIntent.putExtra("REQUEST_NAME", "SendNotification");
					sendNotificationIntent.putExtra("destination", notiDestination[position]);
					sendNotificationIntent.putExtra("time", notiTime[position]);
					sendNotificationIntent.putExtra("startPoint", notiStart[position]);
					startActivityForResult(sendNotificationIntent, 1501);
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
