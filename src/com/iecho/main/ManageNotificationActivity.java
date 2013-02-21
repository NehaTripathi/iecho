package com.iecho.main;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Toast;

import com.iecho.database.DataBaseConnectionManager;
import com.iecho.services.SendUpdatedLocationService;

public class ManageNotificationActivity extends Activity {
	private ListView detailListView;
	private Context context;
	private ListViewCustomAdapter adapter;
	private DataBaseConnectionManager connectionManager;
	private ArrayList<ArrayList<String>> arrayList;
	private String[] notiName,notiDestination,startLocation,notiTime,notiUserId,rowId;
	private int notificationCount; 
	private TextView notificationNumberTextView;
	private Button homeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_notification);
		context=this;
		setUpViews();
		Button infoButton=(Button)findViewById(R.id.manage_noti_in_info);
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
				setResult(1542);
				finish();
			}
		});

		connectionManager=new DataBaseConnectionManager(context);
		getNotifications();
	}


	private void getNotifications(){
		arrayList=connectionManager.getNotificationDetails();
		System.out.println("array list size : "+arrayList.size());
		notificationCount=arrayList.size();
		notificationNumberTextView.setText(notificationCount+" Destination");
		notiName=new String[arrayList.size()];
		notiDestination=new String[arrayList.size()];
		startLocation=new String[arrayList.size()];
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
			notiName[i]=innerArrayList.get(0);
			notiDestination[i]=innerArrayList.get(1);
			startLocation[i]=innerArrayList.get(2);
			notiTime[i]=innerArrayList.get(3);
			notiUserId[i]=innerArrayList.get(4);
			rowId[i]=innerArrayList.get(5);
		}
		adapter=new ListViewCustomAdapter(ManageNotificationActivity.this, notiName, notiDestination);
		detailListView.setAdapter(adapter);
	}

	private void setUpViews(){
		notificationNumberTextView=(TextView)findViewById(R.id.manage_noti_inc_req_number_text);
		detailListView=(ListView)findViewById(R.id.manage_noti_inc_req_list);
		homeButton=(Button)findViewById(R.id.manage_noti_inc_req_home_btn);
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
				convertView = inflater.inflate(R.layout.custom_row_notification, null);
				holder.imgViewDetail = (ImageView) convertView.findViewById(R.id.imgViewLogo);
				holder.imgViewAccept = (ImageView) convertView.findViewById(R.id.imgViewReject);
				holder.imgViewReject = (ImageView) convertView.findViewById(R.id.imgViewAccept);
				holder.txtViewTitle = (TextView) convertView.findViewById(R.id.txtViewTitle);
//				holder.imgViewDetail.setVisibility(View.INVISIBLE);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();

			holder.imgViewDetail.setImageResource(R.drawable.btn_arrow);
			holder.imgViewAccept.setImageResource(R.drawable.edit);
			holder.imgViewReject.setImageResource(R.drawable.delete);
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
					final Dialog dialog_inn = new Dialog(context);
					dialog_inn.setContentView(R.layout.custom_dialog);
					dialog_inn.setTitle("Confirm delete destination ");
					dialog_inn.getWindow().setTitleColor(Color.parseColor("#008bd0"));
					dialog_inn.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
					Button button1 = (Button) dialog_inn.findViewById(R.id.dialog_btn1);
					button1.setText("Delete");
					Button button2= (Button) dialog_inn.findViewById(R.id.dialog_btn2);
					button2.setText("Cancel");

					button1.setVisibility(View.VISIBLE);
					button2.setVisibility(View.VISIBLE);

					button1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							try{
								connectionManager.deleteNotification(Long.parseLong(rowId[position]));
								dialog_inn.dismiss();
								getNotifications();
								Toast.makeText(context, "Destination deleted successfully", Toast.LENGTH_LONG).show();
							}catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					button2.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							try{
								dialog_inn.dismiss();
							}catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

					dialog_inn.show();

				}
			});
			holder.imgViewAccept.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent editNotificationIntent=new Intent(context, NotificationEditActivity.class);
					editNotificationIntent.putExtra("rowid", rowId[position]);
					editNotificationIntent.putExtra("name", notiName[position]);
					editNotificationIntent.putExtra("destination", notiDestination[position]);
					editNotificationIntent.putExtra("startLocation", startLocation[position]);
					editNotificationIntent.putExtra("time", notiTime[position]);
					editNotificationIntent.putExtra("userid", notiUserId[position]);
					startActivityForResult(editNotificationIntent, 150);
				}
			});

			return convertView;
		}

	}

	public void showDialog(){
		final Dialog dialog_inn = new Dialog(context);
		dialog_inn.setContentView(R.layout.custom_dialog);
		dialog_inn.setTitle("Maintain friend as         ");
		dialog_inn.getWindow().setTitleColor(Color.parseColor("#008bd0"));
		dialog_inn.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
		Button button1 = (Button) dialog_inn.findViewById(R.id.dialog_btn1);
		button1.setText("Private Contact");
		Button button2= (Button) dialog_inn.findViewById(R.id.dialog_btn2);
		button2.setText("Public Contact ");

		button1.setVisibility(View.VISIBLE);
		button2.setVisibility(View.VISIBLE);

		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try{

				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try{

				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		dialog_inn.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode==1151) {
			try {
				getNotifications();				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(resultCode==1245){
			setResult(1542);
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
