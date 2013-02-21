package com.iecho.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.database.DataBaseConnectionManager;
import com.iecho.services.LocationListUpdateService;

public class CreateNewNotificationActivity extends Activity {
	private Button homeButton,saveNotificationButton;
	private EditText notificationNameEditText/*,startingLocationEditText,locationEditText*/,timeEditText;
	private EditText startingLocationSpinner, endLocationSpinner;
	public Context context;
	private TextView errorTextView;
	private DataBaseConnectionManager connectionManager;
	private int notificationCount;
//	private ArrayAdapter<String> startingArrayAdapter,endArrayAdapter;
	private String location="",startLocation="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_new_notification);
		context=this;
		setUpViews();
//		SharedPreferences sharedPreferences = getSharedPreferences("locationPref"+ApplicationUtility.USER_ID, MODE_PRIVATE);
//		String endList = sharedPreferences.getString("endList", null);
//		String startList = sharedPreferences.getString("startList", null);
		//		String[] endArray = endList.split("||");
		//		String[] startArray = startList.split("||");

		//		ArrayList<String> arrayList = new ArrayList<String>();
		//		String value = "";
		//		for (int i = 0; i < endList.length(); i++) {
		//			
		//		}

		connectionManager=new DataBaseConnectionManager(context);
		checkNotificationCounts();
		Button infoButton=(Button)findViewById(R.id.create_new_noti_info);
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
				setResult(205);
				finish();
			}
		});
		try {
			startingLocationSpinner.setText(LocationListUpdateService.currentLocationName);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		if(LocationListUpdateService.locationNames!=null){
//			try{
//				String[] endLocationsArray = new String[LocationListUpdateService.locationNames.length+1];
//				startingArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, LocationListUpdateService.startingLocationNames);
//				endLocationsArray[0]="Select";
//				for (int i = 0; i < LocationListUpdateService.locationNames.length; i++) {
//					endLocationsArray[i+1] = LocationListUpdateService.locationNames[i];
//				}
//				endArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, endLocationsArray);
//
//				startingLocationSpinner.setAdapter(startingArrayAdapter);
//				endLocationSpinner.setAdapter(endArrayAdapter);
//			}catch (Exception e) {
//				e.printStackTrace();
//			}
//		}

//		startingLocationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
//				startLocation = arg0.getItemAtPosition(arg2).toString();
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//
//			}
//		});

//		endLocationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
//				location = arg0.getItemAtPosition(arg2).toString();
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//
//			}
//		});

		saveNotificationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String name=notificationNameEditText.getText().toString();
				String time=timeEditText.getText().toString();
				String startLocation = startingLocationSpinner.getText().toString();
				String location = endLocationSpinner.getText().toString();
					if (name.equals("") || location.equals("") ||startLocation.equals("")|| time.equals("")||name.startsWith(" ")||location.startsWith(" ")||time.startsWith(" ")||startLocation.startsWith(" ")) {
						new ApplicationUtility(context, CreateNewNotificationActivity.this).errorDialogAlertBox("Fill all fields with valid data", "Create Destination error!", R.drawable.icon);
					} else {
						long rowid=connectionManager.addNewNotification(name, location,startLocation ,time, ApplicationUtility.USER_ID);
						System.out.println("notification added at >>>> "+rowid);
						Toast.makeText(context, "Destination added successfully", Toast.LENGTH_LONG).show();
						finish();
						checkNotificationCounts();
					}
				
				
			}
		});
	}

	private void setUpViews() {
		homeButton = (Button) findViewById(R.id.create_new_noti_home_btn);
		saveNotificationButton= (Button) findViewById(R.id.create_new_noti_save_btn);
		notificationNameEditText= (EditText) findViewById(R.id.create_new_noti_name_edit);
		//locationEditText= (EditText) findViewById(R.id.create_new_noti_location_edit);
		timeEditText= (EditText) findViewById(R.id.create_new_noti_time_edit);
		errorTextView=(TextView)findViewById(R.id.create_new_noti_error_text);
		startingLocationSpinner = (EditText)findViewById(R.id.create_new_noti_start_location_edit);
		endLocationSpinner = (EditText)findViewById(R.id.create_new_noti_location_edit);
		//startingLocationEditText= (EditText) findViewById(R.id.create_new_noti_start_location_edit);
	}

	private void checkNotificationCounts(){
		System.out.println(connectionManager.getRowCount("iecho_table3_db_1"));
		notificationCount=connectionManager.getRowCount("iecho_table3_db_1");
		if(notificationCount>4){
			notificationNameEditText.setEnabled(false);
			//locationEditText.setEnabled(false);
			timeEditText.setEnabled(false);
			//startingLocationEditText.setEnabled(false);
			saveNotificationButton.setVisibility(View.GONE);
			errorTextView.setVisibility(View.VISIBLE);
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
