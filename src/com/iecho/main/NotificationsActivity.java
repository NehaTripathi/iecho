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

import com.iecho.services.SendUpdatedLocationService;

public class NotificationsActivity extends Activity {
	private Button homeButton,createNotificationButton,manageNotificationButton,sendNotificationButton,cancelButton;
	public Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notifications);
		context=this;
		setUpViews();

		Button infoButton=(Button)findViewById(R.id.notification_info);
		infoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent infoIntent=new Intent(context, AboutIechoActivity.class);
				startActivity(infoIntent);
			}
		});

		sendNotificationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent sendNotificationIntent=new Intent(context, SendNotificationActivity.class);
				startActivityForResult(sendNotificationIntent, 541);

			}
		});

		homeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		manageNotificationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent manageNotiIntent=new Intent(context, ManageNotificationActivity.class);
				startActivityForResult(manageNotiIntent, 141);
			}
		});

		createNotificationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent createNotificationIntent=new Intent(context,CreateNewNotificationActivity.class);
				startActivityForResult(createNotificationIntent, 110);
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	private void setUpViews() {
		homeButton = (Button) findViewById(R.id.notification_home_btn);
		createNotificationButton=(Button) findViewById(R.id.notifications_create_notification_btn);
		manageNotificationButton=(Button) findViewById(R.id.notificatios_manage_noti_btn);
		sendNotificationButton=(Button) findViewById(R.id.notifications_send_noti_btn);
		cancelButton=(Button) findViewById(R.id.notifications_cancel_btn);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==205) {
			finish();
		}else if(resultCode==1542){
			finish();
		}else if(resultCode==1452){
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
