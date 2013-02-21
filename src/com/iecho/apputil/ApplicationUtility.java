package com.iecho.apputil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.iecho.main.R;
import com.iecho.services.SendUpdatedLocationService;

public class ApplicationUtility {
	private ConnectivityManager connectivityManager;
	private NetworkInfo networkInfo;
	private Context context;
	public boolean connectivityAvailable = false;
	private Activity activity;
	public static String USER_ID="";
	//public static String SERVICE_URL="http://192.168.0.119/iEchoMobileService.svc";
	public static String SERVICE_URL="http://wcfservice.iechomobility.com/iEchoMobileService.svc";
	public static String DB_NAME="";
	public static String PASSWORD="";
	public static String USER_CURRENT_STATE="Public";
	public static String IS_SUBSCRIBED_USER="NO";
	public static String EMERGENCY_SMS="This is an emergency SOS message. I am currently in problem.";	
	//public static String EMERGENCY_SMS_WITH_LOCATION="This is an emergency SOS message. I am currently in problem. I am currently at "+SendUpdatedLocationService.CURRENT_LOCATION_NAME;
//	public static String SUBSCRIPTION_URL="http://192.168.0.89:101/mobilepayment.aspx?email="+ApplicationUtility.DB_NAME+"&password="+ApplicationUtility.PASSWORD;

	public ApplicationUtility(Context context,Activity activity) {
		this.context=context;
		this.activity=activity;
	}

	public ApplicationUtility(Context context) {
		this.context=context;
	}
	/**
	 * 
	 * @return true when any network available and false if no network is there
	 */
	public boolean checkNetworkConnection(){
		try{
			connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			networkInfo = connectivityManager.getActiveNetworkInfo();
			try {
				if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
					System.out.println(networkInfo.getTypeName());
					connectivityAvailable = true;
				}
				if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					System.out.println(networkInfo.getTypeName());
					connectivityAvailable = true;
				}	
			} catch (Exception e) {
				System.out.println("connectivity manager>>>>>> exception>>>>>> "+e );
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return connectivityAvailable;
	}

	public void dialogAlertBox(){

		final Dialog dialog_inner_1 = new Dialog(context);
		dialog_inner_1.setContentView(R.layout.custom_dialog_error);
		dialog_inner_1.setTitle("Network Error!");
		dialog_inner_1.getWindow().setTitleColor(Color.parseColor("#008bd0"));
		dialog_inner_1.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
		TextView text1 = (TextView) dialog_inner_1.findViewById(R.id.dialog_error_text1);
		text1.setText("No network available.");
		text1.setTextColor(Color.parseColor("#008bd0"));
		TextView text2 = (TextView) dialog_inner_1.findViewById(R.id.dialog_error_text2);
		text2.setText("Please check device settings.");
		text2.setTextColor(Color.parseColor("#008bd0"));
		text2.setVisibility(View.VISIBLE);
		Button button1= (Button) dialog_inner_1.findViewById(R.id.dialog_error_btn1);		
		button1.setText("OK");
		button1.setVisibility(View.VISIBLE);
		text1.setVisibility(View.VISIBLE);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				activity.finish();

			}
		});

		dialog_inner_1.show();
	}

	public void errorDialogAlertBox(String message,String title,int iconId){

		final Dialog dialog_inner_1 = new Dialog(context);
		dialog_inner_1.setContentView(R.layout.custom_dialog_error);
		dialog_inner_1.setTitle(title);
		dialog_inner_1.getWindow().setTitleColor(Color.parseColor("#008bd0"));
		dialog_inner_1.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
		TextView text1 = (TextView) dialog_inner_1.findViewById(R.id.dialog_error_text1);
		text1.setText(message);
		text1.setTextColor(Color.parseColor("#008bd0"));
		Button button1= (Button) dialog_inner_1.findViewById(R.id.dialog_error_btn1);		
		button1.setText("OK");
		button1.setVisibility(View.VISIBLE);
		text1.setVisibility(View.VISIBLE);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog_inner_1.dismiss();
			}
		});

		dialog_inner_1.show();
	}
}
