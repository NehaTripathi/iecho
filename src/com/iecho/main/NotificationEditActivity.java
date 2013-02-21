package com.iecho.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.database.DataBaseConnectionManager;
import com.iecho.services.SendUpdatedLocationService;

public class NotificationEditActivity extends Activity {
	private Button homeButton,saveNotificationButton;
	private EditText notificationNameEditText,locationEditText,startLocationEditText,timeEditText;
	public Context context;
	private DataBaseConnectionManager connectionManager;
	private String rowId="",notiName="",notiDestination="",startLocation="",notiUserId="",notiTime="";
	private Intent intent;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_edit);
		setUpViews();
		context=this;
		connectionManager=new DataBaseConnectionManager(context);
		intent=getIntent();
		Bundle bundle=intent.getExtras();
		rowId=bundle.getString("rowid");
		notiName=bundle.getString("name");
		notiDestination=bundle.getString("destination");
		startLocation=bundle.getString("startLocation");
		notiUserId=bundle.getString("userid");
		notiTime=bundle.getString("time");

		notificationNameEditText.setText(notiName);
		locationEditText.setText(notiDestination);
		timeEditText.setText(notiTime);
		startLocationEditText.setText(startLocation);

		Button infoButton=(Button)findViewById(R.id.edit_noti_info);
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
				setResult(1245);
				finish();
			}
		});


		saveNotificationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String name=notificationNameEditText.getText().toString();
				String location=locationEditText.getText().toString();
				String startLocation=startLocationEditText.getText().toString();
				String time=timeEditText.getText().toString();
				if (name.equals("") || location.equals("") ||startLocation.equals("")|| time.equals("")||name.startsWith(" ")||location.startsWith(" ")||time.startsWith(" ")||startLocation.startsWith(" ")) {
					new ApplicationUtility(context, NotificationEditActivity.this).errorDialogAlertBox("Fill all fields with valid data", "Edit Destination error!", R.drawable.icon);
				} else {
					long rowid=connectionManager.updateNotification(name, location,startLocation, time, notiUserId,rowId);
					System.out.println("notification added at >>>> "+rowid);
					Toast.makeText(context, "Destination edited successfully", Toast.LENGTH_LONG).show();
					setResult(1151);
					finish();
				}
			}
		});
	}

	private void setUpViews() {
		homeButton = (Button) findViewById(R.id.edit_noti_home_btn);
		saveNotificationButton= (Button) findViewById(R.id.edit_noti_save_btn);
		notificationNameEditText= (EditText) findViewById(R.id.edit_noti_name_edit);
		locationEditText= (EditText) findViewById(R.id.edit_noti_location_edit);
		timeEditText= (EditText) findViewById(R.id.edit_noti_time_edit);
		startLocationEditText=(EditText)findViewById(R.id.edit_noti_start_location_edit);
	}

	@Override
	protected void onPause() {
		super.onPause();
		connectionManager.closeDB();
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
