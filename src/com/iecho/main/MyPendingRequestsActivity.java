package com.iecho.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.services.SendUpdatedLocationService;

public class MyPendingRequestsActivity extends Activity{
	private Button homeButton,followYouButton,findMeButton,friendMeButton;
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pending_requests);
		context=this;
		setUpViews();
		Button infoButton=(Button)findViewById(R.id.pending_request_info);
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
				setResult(404);
				finish();
			}
		});

		followYouButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(new ApplicationUtility(context).checkNetworkConnection()){
					if(ApplicationUtility.USER_CURRENT_STATE.equals("Public")||ApplicationUtility.USER_CURRENT_STATE.equals("Secret")){
						Toast.makeText(context, "No pending requests as you are in "+ApplicationUtility.USER_CURRENT_STATE+" state", Toast.LENGTH_LONG).show();
					}else {
						Intent followYouPendingIntent=new Intent(context, FollowYouIncomingRequestActivity.class);
						startActivityForResult(followYouPendingIntent, 602);
					}
				}else{
					Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
				}
			}
		});

		findMeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(new ApplicationUtility(context).checkNetworkConnection()){
					if(ApplicationUtility.USER_CURRENT_STATE.equals("Public")||ApplicationUtility.USER_CURRENT_STATE.equals("Secret")){
						Toast.makeText(context, "No pending requests as you are in "+ApplicationUtility.USER_CURRENT_STATE+" state", Toast.LENGTH_LONG).show();
					}else {
						Intent findMePendingIntent=new Intent(context, FindMeIncomingRequestActivity.class);
						startActivityForResult(findMePendingIntent, 602);
					}
				}else{
					Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
				}
			}
		});

		//		friendMeButton.setOnClickListener(new OnClickListener() {
		//
		//			@Override
		//			public void onClick(View v) {
		//				Intent friendRequestIntent=new Intent(context,FriendMeOutgoingPendingReqActivity.class);
		//				startActivityForResult(friendRequestIntent, 418);
		//			}
		//		});
	}

	private void setUpViews(){
		homeButton=(Button)findViewById(R.id.pending_requests_home_btn);
		followYouButton=(Button)findViewById(R.id.pending_requests_follow_you_btn);
		findMeButton=(Button)findViewById(R.id.pending_requests_fine_me_btn);
		friendMeButton=(Button)findViewById(R.id.pending_requests_friend_me_btn);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem menuItem = menu.add(0, 1, 0, "Follow You List");
		MenuItem menuItem2 = menu.add(0, 2, 0, "Find Me List");
		menuItem.setIcon(R.drawable.icon_menu_follow);
		menuItem2.setIcon(R.drawable.icon_menu_findme);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(new ApplicationUtility(context).checkNetworkConnection()){
			if (item.getItemId() == 1) {
				Intent acceptedFollowYouIntent=new Intent(context,ViewAcceptedFollowYouRequestActivity.class);
				startActivityForResult(acceptedFollowYouIntent, 850);
			} else if (item.getItemId() == 2) {
				Intent acceptedFindMeIntent=new Intent(context,ViewAcceptedFindMeRequestActivity.class);
				startActivityForResult(acceptedFindMeIntent, 750);
			}
		}else{
			Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==418){
			setResult(404);
			finish();
		}else if(resultCode==1290){
			setResult(404);
			finish();
		}else if(resultCode==1484){
			setResult(404);
			finish();
		}else if(resultCode==1524){
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
	public void onAttachedToWindow(){  
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
