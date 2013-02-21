package com.iecho.main;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class DeviceContactsActivity extends ListActivity {
	private String[] from = {
			ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
			ContactsContract.CommonDataKinds.Phone.NUMBER,
			ContactsContract.CommonDataKinds.Phone._ID};
	private ListView lv;
	private Cursor Cursor1;

	@Override
	public long getSelectedItemId() {

		return super.getSelectedItemId();
	}

	@Override
	public int getSelectedItemPosition() {

		return super.getSelectedItemPosition();
	}
	

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//create a cursor to query the Contacts on the device to start populating a listview
		Cursor1 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+ " ASC");
		startManagingCursor(Cursor1);

		int[] to = {android.R.id.text1 , android.R.id.text2}; //sets the items from above string to listview

		//new listadapter, created to use android checked template
		SimpleCursorAdapter listadapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, Cursor1, from, to );
		setListAdapter(listadapter);

		//adds listview so I can get data from it
		lv = getListView();
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Cursor1.moveToPosition(arg2);
				Intent intent = new Intent();
				intent.putExtra("name", Cursor1.getString(Cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
				intent.putExtra("number", Cursor1.getString(Cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
				setResult(200, intent);
				finish();
			}
		});
	}
}












