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
import android.widget.LinearLayout;

import com.iecho.services.SendUpdatedLocationService;

public class AboutIechoActivity extends Activity {
	private LinearLayout linearLayout;
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=this;
		setContentView(R.layout.about_iecho);
		linearLayout=(LinearLayout)findViewById(R.id.about_iecho_main_layout);
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
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
