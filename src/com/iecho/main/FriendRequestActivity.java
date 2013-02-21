package com.iecho.main;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.database.DataBaseConnectionManager;
import com.iecho.vo.ContactVO;
import com.iecho.webservice.WebServiceConnection;

public class FriendRequestActivity extends Activity implements OnItemClickListener{
	private Button homeButton;
	private TextView requestNumberTextView;
	private ListView detailListView;
	private Context context;
	private ListViewCustomAdapter adapter;
	private ProgressDialog progressDialog;
	private Handler listHandler;
	private WebServiceConnection serviceConnection;
	private JSONObject responseJsonObject;
	private JSONArray aryJSONStrings;
	private String[] fName,lName,phoneNumber,email,userId;
	private String contactType="";
	private DataBaseConnectionManager connectionManager;
	private String groupName="";
	private Handler addContactHandler;
	private String firstName,lastName,contactName,userEmail,userid,image_url="";
	private ArrayList<byte[]> imageArrayList;
	private byte[] acceptedImageArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_request);
		context=this;
		setUpViews();

		imageArrayList=new ArrayList<byte[]>();
		Button infoButton=(Button)findViewById(R.id.friend_request_info);
		infoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent infoIntent=new Intent(context, AboutIechoActivity.class);
				startActivity(infoIntent);
			}
		});


		progressDialog=ProgressDialog.show(context, "", "please wait...");

		addContactHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {

			}
		};

		listHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(progressDialog!=null){
					progressDialog.dismiss();
				}
				int value=msg.what;
				if (value==0) {
					Toast.makeText(context, "Request declined successfully", Toast.LENGTH_LONG).show();
				} else if(value==1) {
					Toast.makeText(context, "Request accepted successfully", Toast.LENGTH_LONG).show();
				}
				try {
					afterResponse();
					requestNumberTextView.setText(aryJSONStrings.length()+" Pending Request");
					adapter = new ListViewCustomAdapter(FriendRequestActivity.this, fName, lName);
					detailListView.setAdapter(adapter);
				} catch (Exception e) {

				}
			}
		};


		new Thread(){
			@Override
			public void run() {
				serviceConnection=new WebServiceConnection("VIEW_IN_PENDING_FRIEND_REQUESTS", "http://tempuri.org/IiEchoMobileService/ViewFriendRequest", "http://tempuri.org/", "ViewFriendRequest", ApplicationUtility.SERVICE_URL, "customerId",  ApplicationUtility.USER_ID);
				listHandler.sendEmptyMessage(2);
			};
		}.start();

		homeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				setResult(4182);
				finish();
			}
		});

	}

	private void setUpViews(){
		homeButton=(Button)findViewById(R.id.friend_req_home_btn);
		requestNumberTextView=(TextView)findViewById(R.id.friend_req_number_text);
		detailListView=(ListView)findViewById(R.id.friend_req_list);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Toast.makeText(this,"Title => " + fName[arg2] + " n Description => "+ lName[arg2], Toast.LENGTH_SHORT).show();
	}

	private void afterResponse(){
		try {
			responseJsonObject=serviceConnection.jsonResponse();

			if (responseJsonObject!=null) {

				aryJSONStrings = new JSONArray(responseJsonObject.getString("ContactList"));
				fName=new String[aryJSONStrings.length()];
				lName=new String[aryJSONStrings.length()];
				phoneNumber=new String[aryJSONStrings.length()];
				email=new String[aryJSONStrings.length()];
				userId=new String[aryJSONStrings.length()];
				for (int i = 0; i < aryJSONStrings.length(); i++) {
					JSONObject jsonObject = aryJSONStrings.getJSONObject(i);
					try{
						Log.i("ManageFriendsId >>>>> ", jsonObject.getString("CustomerId"));
						userId[i]=jsonObject.getString("CustomerId");
					} catch (Exception e) {

					}try{
						Log.i("FirstName >>>>> ", jsonObject.getString("FirstName"));
						fName[i]=jsonObject.getString("FirstName");
					} catch (Exception e) {

					}try{
						Log.i("LastName >>>>> ", jsonObject.getString("LastName"));
						lName[i]=jsonObject.getString("LastName");
					} catch (Exception e) {

					}try{
						Log.i("MobileNumber >>>>> ", jsonObject.getString("Phone"));
						phoneNumber[i]=jsonObject.getString("Phone");
					} catch (Exception e) {

					}try{
						Log.i("EmailId >>>>> ", jsonObject.getString("EmailId"));
						email[i]=jsonObject.getString("EmailId");
					} catch (Exception e) {

					}try{
						Log.i("ImageURL >>>>> ", jsonObject.getString("ImageURL"));
						image_url = jsonObject.getString("ImageURL");
					} catch (Exception e) {

					}
					if(!image_url.equals("")){
						fetchUserImage(image_url);
					}
					try{
						imageArrayList.add(friendImageByteArray);
					}catch (Exception e) {
						e.printStackTrace();
					}
					friendImageByteArray = null;
				}
			}

		} catch (Exception e) {

		}

	}

	private byte[] friendImageByteArray;


	private void fetchUserImage(String imageURL){
		try {
			URL url = new URL(imageURL);
			HttpGet httpRequest = null;
			httpRequest = new HttpGet(url.toURI());
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(httpRequest);
			HttpEntity entity = response.getEntity();
			BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
			InputStream input = b_entity.getContent();
			Bitmap bitmap = BitmapFactory.decodeStream(input);
			//			imageView.setImageBitmap(bitmap);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			friendImageByteArray = stream.toByteArray();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	class ListViewCustomAdapter extends BaseAdapter {
		public String title[];
		public String description[];
		public Activity context;
		public LayoutInflater inflater;

		public ListViewCustomAdapter(Activity context, String[] title,String[] description) {
			super();

			this.context = context;
			this.title = title;
			this.description = description;

			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {

			return title.length;
		}

		@Override
		public Object getItem(int position) {

			return null;
		}

		@Override
		public long getItemId(int position) {

			return 0;
		}

		public class ViewHolder {
			ImageView imgViewDetail,imgViewReject,imgViewAccept;
			TextView txtViewTitle;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {


			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.custom_row, null);
				holder.imgViewDetail = (ImageView) convertView.findViewById(R.id.imgViewLogo);
				holder.imgViewAccept = (ImageView) convertView.findViewById(R.id.imgViewReject);
				holder.imgViewReject = (ImageView) convertView.findViewById(R.id.imgViewAccept);
				holder.txtViewTitle = (TextView) convertView.findViewById(R.id.txtViewTitle);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();

			holder.imgViewDetail.setImageResource(R.drawable.btn_arrow);
			holder.imgViewAccept.setImageResource(R.drawable.btn_plus);
			holder.imgViewReject.setImageResource(R.drawable.btn_cross);
			holder.txtViewTitle.setText(title[position]);

			holder.txtViewTitle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(context, title[position], 3000).show();
				}
			});
			holder.imgViewDetail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//					Toast.makeText(context, title[position], 3000).show();
					Intent friendDetailIntent=new Intent(context,ViewFriendProfileActivity.class);
					friendDetailIntent.putExtra("fName", fName[position]);
					friendDetailIntent.putExtra("lName", lName[position]);
					friendDetailIntent.putExtra("number", phoneNumber[position]);
					friendDetailIntent.putExtra("email", email[position]);
					friendDetailIntent.putExtra("userId", userId[position]);
					context.startActivityForResult(friendDetailIntent, 555);
				}
			});
			holder.imgViewReject.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(new ApplicationUtility(context).checkNetworkConnection()){
						final Dialog dialog = new Dialog(context);
						dialog.setContentView(R.layout.custom_dialog);
						dialog.setTitle("Decline Request             ");
						dialog.getWindow().setTitleColor(Color.parseColor("#008bd0"));
						dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
						Button button1 = (Button) dialog.findViewById(R.id.dialog_btn1);
						button1.setText("Yes");
						Button button2= (Button) dialog.findViewById(R.id.dialog_btn2);
						button2.setText("No");

						button1.setVisibility(View.VISIBLE);
						button2.setVisibility(View.VISIBLE);

						button1.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
								progressDialog=ProgressDialog.show(context, "", "please wait...");
								new Thread(){
									@Override
									public void run() {
										serviceConnection=new WebServiceConnection("DECLINE_NEW_FRIEND_REQUEST", "http://tempuri.org/IiEchoMobileService/DeclineFriendRequest", "http://tempuri.org/", "DeclineFriendRequest", ApplicationUtility.SERVICE_URL,"customerId","requestId",ApplicationUtility.USER_ID,userId[position]);
										serviceConnection=new WebServiceConnection("VIEW_IN_PENDING_FRIEND_REQUESTS", "http://tempuri.org/IiEchoMobileService/ViewFriendRequest", "http://tempuri.org/", "ViewFriendRequest", ApplicationUtility.SERVICE_URL, "customerId",  ApplicationUtility.USER_ID);
										listHandler.sendEmptyMessage(0);
									};
								}.start();
							}
						});
						button2.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {

								dialog.dismiss();

							}
						});
						dialog.show();


					}else{
						Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
					}
				}
			});
			holder.imgViewAccept.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					firstName=fName[position];
					lastName=lName[position];
					contactName=phoneNumber[position];
					userEmail=email[position];
					userid=userId[position];
					acceptedImageArray = imageArrayList.get(position);
					
					final Dialog dialog = new Dialog(context);
					dialog.setContentView(R.layout.custom_dialog);
					dialog.setTitle("Select group             ");
					dialog.getWindow().setTitleColor(Color.parseColor("#008bd0"));
					dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
					Button button1 = (Button) dialog.findViewById(R.id.dialog_btn1);
					button1.setText("Choose groups to add");
					Button button2= (Button) dialog.findViewById(R.id.dialog_btn2);
					button2.setText("Cancel");

					button1.setVisibility(View.VISIBLE);
					button2.setVisibility(View.VISIBLE);

					button1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
							Intent groupChooserIntent=new Intent(context,GroupChooserActivity.class);
							startActivityForResult(groupChooserIntent,504);
						}
					});
					button2.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

							dialog.dismiss();

						}
					});
					dialog.show();

				}
			});

			return convertView;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==1024){
			Bundle bundle=data.getExtras();
			groupName=bundle.getString("group_name");
			connectionManager=new DataBaseConnectionManager(context);
			final Dialog dialog = new Dialog(context);
			dialog.setContentView(R.layout.custom_dialog);
			dialog.setTitle("Maintain friend as         ");
			dialog.getWindow().setTitleColor(Color.parseColor("#008bd0"));
			dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
			Button button1 = (Button) dialog.findViewById(R.id.dialog_btn1);
			button1.setText("Private Contact");
			Button button2= (Button) dialog.findViewById(R.id.dialog_btn2);
			button2.setText("Public Contact ");
			Button button3= (Button) dialog.findViewById(R.id.dialog_btn3);
			button3.setText("Cancel");

			button1.setVisibility(View.VISIBLE);
			button2.setVisibility(View.VISIBLE);
			button3.setVisibility(View.VISIBLE);

			button1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					contactType="Private";
					System.out.println("******"+firstName);
					System.out.println("******"+lastName);
					System.out.println("******"+contactName);
					System.out.println("******"+userEmail);
					System.out.println("******"+contactType);
					System.out.println("******"+groupName);
					System.out.println("******"+userid);
					long rowId=connectionManager.addNewContact(new ContactVO(firstName, lastName, contactName, userEmail, contactType, groupName, "0","1", userid,acceptedImageArray));
					System.out.println("contact added at>>>>>>>> "+rowId);
					dialog.dismiss();

					progressDialog=ProgressDialog.show(context, "", "please wait...");
					new Thread(){
						@Override
						public void run() {
							serviceConnection=new WebServiceConnection("ACCEPT_NEW_FRIEND_REQUEST", "http://tempuri.org/IiEchoMobileService/AcceptFriendRequest", "http://tempuri.org/", "AcceptFriendRequest", ApplicationUtility.SERVICE_URL,"customerId","requestId","friendType",ApplicationUtility.USER_ID,userid,"0");
							serviceConnection=new WebServiceConnection("VIEW_IN_PENDING_FRIEND_REQUESTS", "http://tempuri.org/IiEchoMobileService/ViewFriendRequest", "http://tempuri.org/", "ViewFriendRequest", ApplicationUtility.SERVICE_URL, "customerId",  ApplicationUtility.USER_ID);
							listHandler.sendEmptyMessage(1);
						};
					}.start();
				}
			});
			button2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(new ApplicationUtility(context).checkNetworkConnection()){
						contactType="Public";
						System.out.println("******"+firstName);
						System.out.println("******"+lastName);
						System.out.println("******"+contactName);
						System.out.println("******"+userEmail);
						System.out.println("******"+contactType);
						System.out.println("******"+groupName);
						System.out.println("******"+userid);
						long rowId=connectionManager.addNewContact(new ContactVO(firstName, lastName, contactName, userEmail, contactType, groupName, "0","1",userid,acceptedImageArray));
						System.out.println("contact added at>>>>>>>> "+rowId);
						dialog.dismiss();

						progressDialog=ProgressDialog.show(context, "", "please wait...");
						new Thread(){
							@Override
							public void run() {
								serviceConnection=new WebServiceConnection("ACCEPT_NEW_FRIEND_REQUEST", "http://tempuri.org/IiEchoMobileService/AcceptFriendRequest", "http://tempuri.org/", "AcceptFriendRequest", ApplicationUtility.SERVICE_URL,"customerId","requestId","friendType",ApplicationUtility.USER_ID,userid,"1");
								serviceConnection=new WebServiceConnection("VIEW_IN_PENDING_FRIEND_REQUESTS", "http://tempuri.org/IiEchoMobileService/ViewFriendRequest", "http://tempuri.org/", "ViewFriendRequest", ApplicationUtility.SERVICE_URL, "customerId",  ApplicationUtility.USER_ID);
								listHandler.sendEmptyMessage(1);
							};
						}.start();
					}else{
						Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
					}
				}
			});
			button3.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}
	}

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







