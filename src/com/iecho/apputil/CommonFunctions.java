package com.iecho.apputil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.iecho.main.R;

public class CommonFunctions {
	private Activity mActivity;
	private Context mContext;
	private static CommonFunctions commonFunctions,mCommonFunctions;

	private CommonFunctions(Activity activity,Context context) {
		mActivity = activity;
		mContext = context;
	}

	private CommonFunctions() {

	}

	public static CommonFunctions getInstance(Activity activity, Context context) {
		if (mCommonFunctions == null) {
			mCommonFunctions = new CommonFunctions(activity, context);
		}
		return mCommonFunctions;
	}

	public static CommonFunctions getInstance() {
		if (commonFunctions == null) {
			commonFunctions = new CommonFunctions();
		}
		return commonFunctions;
	}

	/**
	 * 
	 * @param dialogTitle
	 * @param location
	 * @param status
	 */
	public void showDialog(String dialogTitle, String location, String status) {
		final Dialog dialog = new Dialog(mContext);
		dialog.setContentView(R.layout.custom_friend_detail_dialog);
		dialog.setTitle(dialogTitle + "                                  ");
		dialog.getWindow().setTitleColor(Color.parseColor("#008bd0"));
		dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
		Button button1 = (Button) dialog.findViewById(R.id.dialog_btn1);
		button1.setText("Follow Me");
		Button button2 = (Button) dialog.findViewById(R.id.dialog_btn2);
		button2.setText("Find You");
		Button button4 = (Button) dialog.findViewById(R.id.dialog_btn4);
		button4.setText("Cancel");
		TextView statusTextView = (TextView) dialog.findViewById(R.id.statusTextView);
		TextView locationTextView = (TextView) dialog.findViewById(R.id.locationTextView);

		button1.setVisibility(View.VISIBLE);
		button2.setVisibility(View.VISIBLE);
		button4.setVisibility(View.VISIBLE);
		statusTextView.setText(status);
		locationTextView.setText(location);

		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Dialog dialog_inner_1 = new Dialog(mContext);
				dialog_inner_1.setContentView(R.layout.custom_dialog_editbox);
				dialog_inner_1.setTitle("Search by e-mail                   ");
				dialog_inner_1.getWindow().setTitleColor(Color.parseColor("#008bd0"));
				dialog_inner_1.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
				TextView text1 = (TextView) dialog_inner_1.findViewById(R.id.dialog2_txt1);
				text1.setText("Enter email id");
				text1.setTextColor(Color.parseColor("#008bd0"));
				final EditText edit1= (EditText) dialog_inner_1.findViewById(R.id.dialog2_edit1);
				Button button1= (Button) dialog_inner_1.findViewById(R.id.dialog2_btn1);		
				button1.setText("Search");
				Button button2= (Button) dialog_inner_1.findViewById(R.id.dialog2_btn2);	
				button2.setText("Cancel");
				button1.setVisibility(View.VISIBLE);
				button2.setVisibility(View.VISIBLE);
				edit1.setVisibility(View.VISIBLE);
				edit1.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
				text1.setVisibility(View.VISIBLE);
				button1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				});
				button2.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog_inner_1.dismiss();
					}
				});
				dialog_inner_1.show();

				dialog.dismiss();
			}
		});
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		button4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

}