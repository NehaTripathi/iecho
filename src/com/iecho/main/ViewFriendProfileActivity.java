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
import android.widget.TextView;

import com.iecho.services.SendUpdatedLocationService;

public class ViewFriendProfileActivity extends Activity {
	private TextView fNameTextView, lNameTextView, phoneTextView,emailTextView;
	private String fName, lName, phoneNumber, email, userId;
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_friend_profile);
		context=this;
		setUpViews();

		Button infoButton=(Button)findViewById(R.id.view_friend_profile_info);
		infoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent infoIntent=new Intent(context, AboutIechoActivity.class);
				startActivity(infoIntent);
			}
		});

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		fName = bundle.getString("fName");
		lName = bundle.getString("lName");
		phoneNumber = bundle.getString("number");
		email = bundle.getString("email");
		userId = bundle.getString("userId");
		fNameTextView.setText(fName);
		lNameTextView.setText(lName);
		phoneTextView.setText(phoneNumber);
		emailTextView.setText(email);
	}

	private void setUpViews(){
		fNameTextView=(TextView)findViewById(R.id.view_profile_fname_text);
		lNameTextView=(TextView)findViewById(R.id.view_profile_lname_text);
		phoneTextView=(TextView)findViewById(R.id.view_profile_phone_text);
		emailTextView=(TextView)findViewById(R.id.view_profile_email_text);
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
