package com.iecho.main;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.iecho.database.DataBaseConnectionManager;
import com.iecho.services.SendUpdatedLocationService;

public class GroupChooserActivity extends Activity {
	private ListView contactListView;
	private Context context;
	private DataBaseConnectionManager connectionManager;
	private String[] groupArray;
	private Handler handler;
	private ArrayAdapter<String> arrayAdapter;
	private String groupName="";
	private String deleteFlag="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_chooser);
		context=this;
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		try{
			deleteFlag=bundle.getString("group_to_delete");
		}catch (Exception e) {
			//			e.printStackTrace();
		}
		contactListView = (ListView) findViewById(R.id.group_chooser_list);
		handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {

				arrayAdapter=new ArrayAdapter<String>(context, R.layout.spinnerlayout, groupArray);
				arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				contactListView.setAdapter(arrayAdapter);
			}
		};


		new Thread(){
			public void run() {
				connectionManager = new DataBaseConnectionManager(context);
				ArrayList<ArrayList<String>> arrayList = connectionManager.getAllGroups();
				System.out.println("array list length : " + arrayList.size());
				groupArray = new String[arrayList.size()];
				for (int i = 0; i < arrayList.size(); i++) {
					ArrayList<String> innerArrayList = arrayList.get(i);
					System.out.println(innerArrayList.get(0));
					System.out.println(innerArrayList.get(1));
					System.out.println(innerArrayList.get(2));
					groupArray[i]=innerArrayList.get(1);
				}
				handler.sendEmptyMessage(0);
			};
		}.start();

		contactListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				groupName=arg0.getItemAtPosition(arg2).toString();
				System.out.println(">>>>>>>>>>>>>> " + groupName);
				Intent intent=new Intent();
				intent.putExtra("group_name", groupName);
				if(deleteFlag.equals("")){
					setResult(1024, intent);
				}else if(deleteFlag.equals("yes")){
					setResult(1028, intent);
				}
				GroupChooserActivity.this.finish();
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
