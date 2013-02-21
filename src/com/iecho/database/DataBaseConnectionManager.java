package com.iecho.database;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.vo.ContactVO;

public class DataBaseConnectionManager {
	private final String DB_NAME=ApplicationUtility.DB_NAME;
	private final String TABLE_1_NAME="iecho_table1_db_1";
	private final String TABLE_2_NAME="iecho_table2_db_1";
	private final String TABLE_3_NAME="iecho_table3_db_1";
	private final int DB_VERSION=1;
	private SQLiteDatabase sqLiteDatabase;
	private final CustomOpenHelper openHelper;
	public DataBaseConnectionManager(Context context) {
		openHelper=new CustomOpenHelper(context);
		try{
			sqLiteDatabase=openHelper.getWritableDatabase();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeDB(){
		try{
			if(openHelper!=null){
				System.out.println(">>>>>>>>>>>>>>> openHelper.close()");
				openHelper.close();
			}
			if(sqLiteDatabase!=null){
				System.out.println(">>>>>>>>>>>>> sqLiteDatabase.close()");
				sqLiteDatabase.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getFriendDetails(String userID){
		Cursor cursor = sqLiteDatabase.query(TABLE_1_NAME, new String[]{"contact_first_name"}, "user_id ='"+userID+"'", null, null, null, null);
		String username =    "";
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			username = cursor.getString(cursor.getColumnIndex("contact_first_name"));
		}
		cursor.close();
		return username;
	}



	/*public long addNewContact(String fname,String lname,String number,String email,String type,String groupname,String userid,String isWebContact,String isFriend,byte[] friendImage){
		ContentValues contentValues = new ContentValues();
		contentValues.put("contact_first_name", fname);
		contentValues.put("contact_last_name", lname);
		contentValues.put("contact_number", number);
		contentValues.put("contact_email", email);
		contentValues.put("contact_type", type);
		contentValues.put("group_name", groupname);
		contentValues.put("user_id", userid);
		contentValues.put("is_web_contact", isWebContact);
		contentValues.put("is_friend", isFriend);
		contentValues.put("friend_image", friendImage);
		long row_id=sqLiteDatabase.insert(TABLE_1_NAME, null, contentValues);
		return row_id;
	}*/
	
	public long addNewContact(ContactVO vo){
		ContentValues contentValues = new ContentValues();
		contentValues.put("contact_first_name", vo.getFirstName());
		contentValues.put("contact_last_name", vo.getLastName());
		contentValues.put("contact_number", vo.getNumber());
		contentValues.put("contact_email", vo.getEmail());
		contentValues.put("contact_type", vo.getContactType());
		contentValues.put("group_name", vo.getGroupname());
		contentValues.put("user_id", vo.getUserId());
		contentValues.put("is_web_contact", vo.getIsWebContact());
		contentValues.put("is_friend", vo.getIsFriend());
		contentValues.put("friend_image", vo.getImage());
		long row_id=sqLiteDatabase.insert(TABLE_1_NAME, null, contentValues);
		return row_id;
	}

	public long addNewGroup(String groupname,int groupid){
		ContentValues contentValues = new ContentValues();
		contentValues.put("group_name", groupname);
		contentValues.put("group_id", groupid);
		long row_id=sqLiteDatabase.insert(TABLE_2_NAME, null, contentValues);
		return row_id;
	}

	public long addNewNotification(String notificationName,String destName,String startLocName,String avgTime,String userId){
		ContentValues contentValues = new ContentValues();
		contentValues.put("notification_name", notificationName);
		contentValues.put("destination_name", destName);
		contentValues.put("start_name", startLocName);
		contentValues.put("average_time", avgTime);
		contentValues.put("user_id", userId);
		long row_id=sqLiteDatabase.insert(TABLE_3_NAME, null, contentValues);
		return row_id;
	}

	public long updateNotification(String notificationName,String destName,String startLoc,String avgTime,String userId,String row_id){
		ContentValues contentValues = new ContentValues();
		contentValues.put("notification_name", notificationName);
		contentValues.put("destination_name", destName);
		contentValues.put("start_name", startLoc);
		contentValues.put("average_time", avgTime);
		contentValues.put("user_id", userId);
		long rowid=sqLiteDatabase.update(TABLE_3_NAME, contentValues, "row_id ='"+row_id+"'", null);
		return rowid;
	}

	public ArrayList<ArrayList<String>> getNotificationDetails(){
		ArrayList<ArrayList<String>> arrayList=new ArrayList<ArrayList<String>>();

		Cursor cursor=sqLiteDatabase.query(TABLE_3_NAME,  new String[]{"notification_name","destination_name","start_name","average_time","user_id","row_id"} , null, null, null, null, null);
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			ArrayList<String> innerArrayList=new ArrayList<String>();
			String name=cursor.getString(0);
			String dname=cursor.getString(1);
			String strname=cursor.getString(2);
			String time=cursor.getString(3);
			String uid=cursor.getString(4);
			String rowid=cursor.getString(5);
			System.out.println(">>>>>>>result>>>>>>>"+name);
			System.out.println(">>>>>>>result>>>>>>>"+dname);
			System.out.println(">>>>>>>result>>>>>>>"+strname);
			System.out.println(">>>>>>>result>>>>>>>"+time);
			System.out.println(">>>>>>>result>>>>>>>"+uid);
			System.out.println(">>>>>>>result>>>>>>>"+rowid);
			innerArrayList.add(name);
			innerArrayList.add(dname);
			innerArrayList.add(strname);
			innerArrayList.add(time);
			innerArrayList.add(uid);
			innerArrayList.add(rowid);
			arrayList.add(innerArrayList);
		}
		cursor.close();
		return arrayList;
	}

	public int getRowCount(String tableName){
		Cursor mCount= sqLiteDatabase.rawQuery("select count(*) from "+tableName, null);
		mCount.moveToFirst();
		int count= mCount.getInt(0);
		mCount.close();
		return count;
	}

	public ArrayList<ArrayList> getSearchContactResult(String requestParam,String searchParam,String[] searchDBField){
		ArrayList<ArrayList> arrayList=new ArrayList<ArrayList>();

		Cursor cursor=sqLiteDatabase.query(TABLE_1_NAME, searchDBField ,requestParam + " like '"+searchParam+"%'", null, null, null, null);
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			ArrayList innerArrayList=new ArrayList();
			String fname=cursor.getString(0);
			String lname=cursor.getString(1);
			String number=cursor.getString(2);
			String email=cursor.getString(3);
			String userid=cursor.getString(4);
			Bitmap userImage=null;
			try{
				userImage=getImage(userid);
			}catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(">>>>>>>result>>>>>>>"+fname);
			System.out.println(">>>>>>>result>>>>>>>"+lname);
			System.out.println(">>>>>>>result>>>>>>>"+number);
			System.out.println(">>>>>>>result>>>>>>>"+email);
			System.out.println(">>>>>>>result>>>>>>>"+userid);
			innerArrayList.add(fname);
			innerArrayList.add(lname);
			innerArrayList.add(number);
			innerArrayList.add(email);
			innerArrayList.add(userid);
			innerArrayList.add(userImage);
			arrayList.add(innerArrayList);
		}
		//		cursor.close();
		return arrayList;
	}

	public String checkForIdExists(String userId){
		Cursor cursor=sqLiteDatabase.query(TABLE_1_NAME, new String[]{"contact_first_name"} , "user_id ='"+userId+"'", null, null, null, null);
		String fname="";
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			fname=cursor.getString(0);
		}
		cursor.close();
		return fname;
	}

	public String[] getAllUsersId(){
		Cursor cursor=sqLiteDatabase.query(TABLE_1_NAME, new String[]{"user_id"} , "is_web_contact ='"+"0"+"'", null, null, null, null);

		ArrayList<String> list=new ArrayList<String>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			list.add(cursor.getString(0));
		}
		int size=list.size();
		String[] arr=new String[size];
		for (int i = 0; i < arr.length; i++) {
			arr[i]=list.get(i);
			System.out.println("============>>>"+arr[i]);
		}
		cursor.close();
		return arr;
	}

	public ArrayList<ArrayList<String>> getAllGroups(){
		ArrayList<ArrayList<String>> arrayList=new ArrayList<ArrayList<String>>();
		try{
			Cursor cursor=sqLiteDatabase.query(TABLE_2_NAME, new String[]{"row_id","group_name","group_id"}, null, null, null, null, null);
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				ArrayList<String> innerArrayList=new ArrayList<String>();
				String id=cursor.getString(0);
				String group_name=cursor.getString(1);
				String group_id=cursor.getString(2);
				innerArrayList.add(id);
				innerArrayList.add(group_name);
				innerArrayList.add(group_id);
				arrayList.add(innerArrayList);
			}
			cursor.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return arrayList;
	}


	public String[] getAllContacts(String row_id){


		Cursor cursor=sqLiteDatabase.query(TABLE_1_NAME, new String[]{"contact_first_name","user_id"}, "group_name ='"+row_id+"'", null, null, null, null);
		ArrayList<String> list=new ArrayList<String>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			list.add(cursor.getString(0)+"*"+cursor.getString(1));
		}
		int size=list.size();
		String[] arr=new String[size];
		for (int i = 0; i < arr.length; i++) {
			arr[i]=list.get(i);
			System.out.println("============"+arr[i]);
		}
		cursor.close();
		return arr;
	}
	
	public List<ContactVO> getAllContacts(){


		Cursor cursor=sqLiteDatabase.query(TABLE_1_NAME,new String[]{"contact_first_name","contact_last_name","contact_number","contact_email","contact_type","group_name","is_web_contact","is_friend"},null, null, null, null, null);
		ArrayList<ContactVO> list=new ArrayList<ContactVO>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			list.add(new ContactVO(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7)));
		}
		cursor.close();
		return list;
	}

	private byte[] imageByteArray;
	private Bitmap theImage = null;

	public Bitmap getImage(String userID){
		Cursor mNotesCursor=sqLiteDatabase.query(TABLE_1_NAME, new String[]{"friend_image"}, "user_id ='"+userID+"'", null, null, null, null);
		if (mNotesCursor.moveToFirst()) {
			do {
				try{
					imageByteArray   =  mNotesCursor.getBlob(mNotesCursor.getColumnIndex("friend_image"));
					ByteArrayInputStream imageStream = new ByteArrayInputStream(imageByteArray);
					theImage= BitmapFactory.decodeStream(imageStream);
				}catch (Exception e) {
					e.printStackTrace();
					theImage = null;
				}
			} while (mNotesCursor.moveToNext());
		}
		mNotesCursor.close();
		return theImage;
	}

	public String[] getContactId(){


		Cursor cursor=sqLiteDatabase.query(TABLE_1_NAME, new String[]{"user_id"}, null, null, null, null, null);
		ArrayList<String> list=new ArrayList<String>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			list.add(cursor.getString(0));
		}
		int size=list.size();
		String[] arr=new String[size];
		for (int i = 0; i < arr.length; i++) {
			arr[i]=list.get(i);
			System.out.println("============"+arr[i]);
		}
		cursor.close();
		return arr;
	}

	public String getContactType(String userId){

		Cursor cursor=sqLiteDatabase.query(TABLE_1_NAME, new String[]{"contact_type"}, "user_id ='"+userId+"'", null, null, null, null);
		ArrayList<String> list=new ArrayList<String>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			list.add(cursor.getString(0));
		}

		String arr=new String();

		arr=list.get(0);
		cursor.close();
		return arr;
	}

	/**
	 * 
	 * method to delete the web contacts
	 */
	public int deleteWebContects(){
		int rowDeleted=sqLiteDatabase.delete(TABLE_1_NAME, "is_web_contact ='"+1+"'", null);
		return rowDeleted;
	}


	public int deleteContact(long row_id){
		int rowDeleted=sqLiteDatabase.delete(TABLE_1_NAME, "user_id ='"+row_id+"'", null);
		return rowDeleted;
	}

	public int deleteGroup(String groupName){
		int rowDeleted=sqLiteDatabase.delete(TABLE_2_NAME, "group_name ='"+groupName+"'", null);
		return rowDeleted;
	}

	/**
	 * 
	 * @return string array of all the contacts in DB
	 */
	public String[] getContactNumbers(){


		Cursor cursor=sqLiteDatabase.query(TABLE_1_NAME, new String[]{"contact_number"}, null, null, null, null, null);
		ArrayList<String> list=new ArrayList<String>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			list.add(cursor.getString(0));
		}
		int size=list.size();
		String[] arr=new String[size];
		for (int i = 0; i < arr.length; i++) {
			arr[i]=list.get(i);
			System.out.println("============"+arr[i]);
		}
		cursor.close();
		return arr;
	}

	public long updateContectGroup(String groupName){
		ContentValues contentValues = new ContentValues();
		contentValues.put("group_name", "My Group");
		long rowid=sqLiteDatabase.update(TABLE_1_NAME, contentValues, "group_name ='"+groupName+"'", null);
		System.out.println(">>>>"+rowid);
		return rowid;
	}


	public int deleteNotification(long row_id){
		int rowDeleted=sqLiteDatabase.delete(TABLE_3_NAME, "row_id ='"+row_id+"'", null);
		return rowDeleted;
	}

	public long updateContactRow(String fname,String lname,String number,String email,String type,String groupname,String userid,long row_id,String isWebContact){
		ContentValues contentValues = new ContentValues();
		contentValues.put("contact_first_name", fname);
		contentValues.put("contact_last_name", lname);
		contentValues.put("contact_number", number);
		contentValues.put("contact_email", email);
		contentValues.put("contact_type", type);
		contentValues.put("group_name", groupname);
		contentValues.put("user_id", userid);
		contentValues.put("is_web_contact", isWebContact);
		long rowid=sqLiteDatabase.update(TABLE_1_NAME, contentValues, "row_id ='"+row_id+"'", null);
		return rowid;
	}

	public long updateGroup(String groupName,int groupid,long row_id){
		ContentValues contentValues = new ContentValues();
		contentValues.put("group_name", groupName);
		contentValues.put("group_id", groupid);
		long rowid=sqLiteDatabase.update(TABLE_2_NAME, contentValues, "row_id ='"+row_id+"'", null);
		return rowid;
	}

	public long updatePhoneNumber(String number,String firstName,String lastName,String userId,byte[] friendImage){
		ContentValues contentValues = new ContentValues();
		contentValues.put("contact_first_name", firstName);
		contentValues.put("contact_last_name", lastName);
		contentValues.put("contact_number", number);
		contentValues.put("friend_image", friendImage);
		long rowid=sqLiteDatabase.update(TABLE_1_NAME, contentValues, "user_id ='"+userId+"'", null);
		return rowid;
	}

	public long updateContactGroup(String userid,String newGroupName){
		ContentValues contentValues = new ContentValues();
		contentValues.put("group_name", newGroupName);
		long rowid=sqLiteDatabase.update(TABLE_1_NAME, contentValues, "user_id ='"+userid+"'", null);
		return rowid;
	}

	public long updateContactType(String userid,String newType){
		ContentValues contentValues = new ContentValues();
		contentValues.put("contact_type", newType);
		long rowid=sqLiteDatabase.update(TABLE_1_NAME, contentValues, "user_id ='"+userid+"'", null);
		System.out.println("=============row id =========="+rowid);
		return rowid;
	}


	private class CustomOpenHelper extends SQLiteOpenHelper{

		public CustomOpenHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase arg0) {
			String tbl_query_1="Create table "+TABLE_1_NAME+" (row_id integer not null primary key,contact_first_name text," +
					"contact_last_name text,contact_number text,contact_email text, contact_type text,group_name text," +
					"user_id text,is_web_contact text,is_friend text,friend_image blob);";

			String tbl_query_2="Create table "+TABLE_2_NAME+" (row_id integer not null primary key,group_name text,group_id integer);";

			String tbl_query_3="Create table "+TABLE_3_NAME+" (row_id integer not null primary key,notification_name text,destination_name text,start_name text,average_time text,user_id text);";

			String qry="Insert into "+TABLE_2_NAME+" values ('1','My Group','1');";

			arg0.execSQL(tbl_query_1);
			arg0.execSQL(tbl_query_2);
			arg0.execSQL(tbl_query_3);
			arg0.execSQL(qry);
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {


		}

	}


}
