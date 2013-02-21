package com.iecho.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.iecho.services.SendUpdatedLocationService;

public class FriendPendingRequestsActivity extends Activity {
	private Button homeButton,followYouButton,findMeButton,friendMeButton;
	private Context context;
	private SharedPreferences switchModePreferences;
	private boolean user_state;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_pending_request);
		context=this;
		setUpViews();

		Button infoButton=(Button)findViewById(R.id.friend_pending_info);
		infoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent infoIntent=new Intent(context, AboutIechoActivity.class);
				startActivity(infoIntent);
			}
		});

		switchModePreferences=getSharedPreferences("USER_MODE_PREF", MODE_PRIVATE);
		user_state=switchModePreferences.getBoolean("user_mode", false);

		homeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setResult(404);
				finish();
			}
		});

		followYouButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent followYouPendingIntent=new Intent(context, FollowYouIncomingRequestActivity.class);
				startActivityForResult(followYouPendingIntent, 602);
			}
		});

		findMeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (user_state) {

				} else {
					Toast.makeText(context, "No pending requests as you are in Public mode", Toast.LENGTH_LONG).show();
				}

			}
		});



		friendMeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent friendRequestIntent=new Intent(context,FriendRequestActivity.class);
				startActivityForResult(friendRequestIntent, 418);
			}
		});
	}

	private void setUpViews(){
		homeButton=(Button)findViewById(R.id.friend_pending_requests_home_btn);
		followYouButton=(Button)findViewById(R.id.friend_pending_requests_follow_you_btn);
		findMeButton=(Button)findViewById(R.id.friend_pending_requests_fine_me_btn);
		friendMeButton=(Button)findViewById(R.id.friend_pending_requests_friend_me_btn);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==418){
			setResult(404);
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