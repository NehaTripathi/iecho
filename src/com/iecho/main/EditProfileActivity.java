package com.iecho.main;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.webservice.WebServiceConnection;

public class EditProfileActivity extends Activity {
	private Button homeButton,choosePicButton,updateProfileButton,sosImageButton;
	private EditText fNameEditText,lNameEditText,phoneEditText;
	public Context context;
	private Handler userProfileHandler,editHandler;
	private String USER_FIRST_NAME,USER_LAST_NAME,USER_PHONE_NUMBER;
	private WebServiceConnection userProfileServiceConnection,sosImageServiceConnection;
	private HashMap<String, Object> userProfileHashMap;
	private HashMap<String, String> statusHashMap,sosImageHashMap;
	private ProgressDialog progressDialog;
	private ImageView imageView,sosImageView;
	private String image;
	private static final int CAMERA_REQUEST = 1888;
	private Bitmap bitmapImage;
	private byte[] byteArray;
	private byte[] sosByteArray;
	private Bitmap sosBitmapImage;
	private LinearLayout sosLinearLayout;
	private boolean flag=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_profile);
		context = this;
		setUpViews();
		progressDialog=ProgressDialog.show(context, "", "please wait...");
		if (ApplicationUtility.IS_SUBSCRIBED_USER.equals("YES")) {
			sosLinearLayout.setVisibility(View.VISIBLE);
		}
		
		editHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(progressDialog!=null){
					progressDialog.dismiss();
				}
				statusHashMap=userProfileServiceConnection.serviceResponce();
				if(statusHashMap!=null){
					String status=statusHashMap.get("status");
					String statusMsg=statusHashMap.get("statusMsg");
					try {
						if(status.equals("1")){
							Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_LONG).show();
							finish();
						}else{
							Toast.makeText(context, "Profile not updated."+statusMsg, Toast.LENGTH_LONG).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};


		userProfileHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(progressDialog!=null){
					progressDialog.dismiss();
				}
				userProfileHashMap=userProfileServiceConnection.userProfileServiceResponce();
				if (ApplicationUtility.IS_SUBSCRIBED_USER.equals("YES")) {
					sosImageHashMap=sosImageServiceConnection.serviceResponce();
				}
				if(userProfileHashMap!=null){
					USER_FIRST_NAME=(String)userProfileHashMap.get("FirstName");
					USER_LAST_NAME=(String)userProfileHashMap.get("LastName");
					USER_PHONE_NUMBER=(String)userProfileHashMap.get("PhoneNumber");
					image=(String)userProfileHashMap.get("UserPicture");

					fNameEditText.setText(USER_FIRST_NAME);
					lNameEditText.setText(USER_LAST_NAME);
					phoneEditText.setText(USER_PHONE_NUMBER);
					if (!image.equals("")) {
						try {
							URL url = new URL(image);
							HttpGet httpRequest = null;

							httpRequest = new HttpGet(url.toURI());

							HttpClient httpclient = new DefaultHttpClient();
							HttpResponse response = httpclient.execute(httpRequest);

							HttpEntity entity = response.getEntity();
							BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
							InputStream input = b_entity.getContent();

							Bitmap bitmap = BitmapFactory.decodeStream(input);
							imageView.setImageBitmap(bitmap);

							ByteArrayOutputStream stream = new ByteArrayOutputStream();
							bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
							byteArray = stream.toByteArray();

						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				/**
				 * downloading sos image from url
				 */
				if (sosImageHashMap!=null) {
					String status=sosImageHashMap.get("status");
					if (status.equals("true")) {
						String url=sosImageHashMap.get("imageUrl");
						if(!url.equals("")){
							try {
								URL sosurl = new URL(url);
								HttpGet httpRequest = null;

								httpRequest = new HttpGet(sosurl.toURI());

								HttpClient httpclient = new DefaultHttpClient();
								HttpResponse response = httpclient.execute(httpRequest);

								HttpEntity entity = response.getEntity();
								BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
								InputStream input = b_entity.getContent();

								Bitmap bitmap = BitmapFactory.decodeStream(input);
								sosImageView.setImageBitmap(bitmap);

								ByteArrayOutputStream stream = new ByteArrayOutputStream();
								bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
								sosByteArray = stream.toByteArray();

							}catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		};

	
		Button infoButton=(Button)findViewById(R.id.edit_profile_info);
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
				setResult(201);
				finish();
			}
		});

		sosImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showPasswordDialog();
			}
		});

		choosePicButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


				final Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.custom_dialog);
				dialog.setTitle("Choose Picture                 ");
				dialog.getWindow().setTitleColor(Color.parseColor("#008bd0"));
				dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
				Button button1 = (Button) dialog.findViewById(R.id.dialog_btn1);
				button1.setText("Take picture from camera");
				Button button2= (Button) dialog.findViewById(R.id.dialog_btn2);
				button2.setText("Choose picture from gallery");

				button1.setVisibility(View.VISIBLE);
				button2.setVisibility(View.VISIBLE);

				button1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// this will open camera
						Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						dialog.dismiss();
						startActivityForResult(cameraIntent, CAMERA_REQUEST);

					}
				});
				button2.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// this will open gallery
						Intent gallery = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						dialog.dismiss();
						startActivityForResult(gallery, 1);	
					}
				});
				dialog.show();
			}
		});

		updateProfileButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String fName=fNameEditText.getText().toString();
				final String lName=lNameEditText.getText().toString();
				final String phone=phoneEditText.getText().toString();
				System.out.println(fName);
				System.out.println(lName);
				System.out.println(phone);
				System.out.println(byteArray);
				if(fName.equals("")||lName.equals("")||phone.equals("")||fName.startsWith(" ")||lName.startsWith(" ")||phone.startsWith(" ")){
					new ApplicationUtility(context, EditProfileActivity.this).errorDialogAlertBox("Fill all fields with valid data", "Edit Profile error!", R.drawable.icon);
				}else{
					if(new ApplicationUtility(context).checkNetworkConnection()){
						progressDialog=ProgressDialog.show(context, "", "please wait...");
						new Thread(){
							@Override
							public void run() {
								userProfileServiceConnection=new WebServiceConnection("EDIT_USER_PROFILE","http://tempuri.org/IiEchoMobileService/EditCustomer", "http://tempuri.org/", "EditCustomer", ApplicationUtility.SERVICE_URL, "userId", "firstname","lastName","phone","userProfilePic", ApplicationUtility.USER_ID, fName,lName,phone,byteArray);
								if(sosByteArray!=null){
									sosImageServiceConnection=new WebServiceConnection("UPLOAD_SOS_IMAGE", "http://tempuri.org/IiEchoMobileService/SaveSOSRequestImage", "http://tempuri.org/", "SaveSOSRequestImage", ApplicationUtility.SERVICE_URL, "customerId", "userImage",  ApplicationUtility.USER_ID, sosByteArray);
								}
								editHandler.sendEmptyMessage(0);
							};
						}.start();
					}else{
						Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
					}
				}
			}


		});

		new Thread(){
			@Override
			public void run() {
				try{
					System.out.println("getting user profile >>>>>>>>>>>>>>>>");
					userProfileServiceConnection=new WebServiceConnection("GET_LOGIN_USER_PROFILE", "http://tempuri.org/IiEchoMobileService/GetCustomerById", "http://tempuri.org/", "GetCustomerById", ApplicationUtility.SERVICE_URL,"customerId",ApplicationUtility.USER_ID);
					if (ApplicationUtility.IS_SUBSCRIBED_USER.equals("YES")) {
						sosImageServiceConnection=new WebServiceConnection("GET_USER_SOS_IMAGE", "http://tempuri.org/IiEchoMobileService/GetSOSImageUrl", "http://tempuri.org/", "GetSOSImageUrl", ApplicationUtility.SERVICE_URL,"customerId",ApplicationUtility.USER_ID);
					}
					userProfileHandler.sendEmptyMessage(0);
				}catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}


	private boolean showPasswordDialog(){

		final Dialog dialog_inner_1 = new Dialog(context);
		dialog_inner_1.setContentView(R.layout.custom_dialog_editbox);
		dialog_inner_1.setTitle("Change SOS Image                   ");
		dialog_inner_1.getWindow().setTitleColor(Color.parseColor("#008bd0"));
		dialog_inner_1.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
		TextView text1 = (TextView) dialog_inner_1.findViewById(R.id.dialog2_txt1);
		text1.setText("Please enter your password");
		text1.setTextColor(Color.parseColor("#008bd0"));
		final EditText edit1= (EditText) dialog_inner_1.findViewById(R.id.dialog2_edit1);
		Button button1= (Button) dialog_inner_1.findViewById(R.id.dialog2_btn1);		
		button1.setText("Ok");
		Button button2= (Button) dialog_inner_1.findViewById(R.id.dialog2_btn2);	
		button2.setText("Cancel");
		button1.setVisibility(View.VISIBLE);
		button2.setVisibility(View.VISIBLE);
		edit1.setVisibility(View.VISIBLE);
		edit1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		text1.setVisibility(View.VISIBLE);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String value=edit1.getText().toString();
				if (value.equals("")) {
					Toast.makeText(context, "Please enter password", Toast.LENGTH_LONG).show();
				} else {
					if (value.equals(ApplicationUtility.PASSWORD)) {
						dialog_inner_1.dismiss();
						InputMethodManager imm = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(edit1.getWindowToken(), 0);
						flag=true;
						// this will open camera on entering correct password
						Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						startActivityForResult(cameraIntent, 159);
					}else{
						Toast.makeText(context, "Invalid password", Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog_inner_1.dismiss();
			}
		});
		dialog_inner_1.show();
		return flag;
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && requestCode == 1) {
			// result code for gallery
			try{
				Uri selectedImage = data.getData();
				String path = getPath(selectedImage);
				System.out.println("Image path is :"+path);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 4;
				bitmapImage = BitmapFactory.decodeFile(path,options);
				imageView.setImageBitmap(bitmapImage);

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				byteArray = stream.toByteArray();

			}catch (Exception e) {
				System.out.println("gallery exception >>>>>>>>>>>>>>>>>>>>");
				e.printStackTrace();
			}
		}
		// result code for camera
		else if (requestCode == CAMERA_REQUEST) {
			try{
				bitmapImage = (Bitmap) data.getExtras().get("data"); 
				imageView.setImageBitmap(bitmapImage);

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				byteArray = stream.toByteArray();
			}catch (Exception e) {
				System.out.println("camera exception >>>>>>>>>>>>>>>>>>>>");
				e.printStackTrace();
			}
		} else if (requestCode == 159) {
			try{
				sosBitmapImage = (Bitmap) data.getExtras().get("data"); 
				sosImageView.setImageBitmap(sosBitmapImage);

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				sosBitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				sosByteArray = stream.toByteArray();
			}catch (Exception e) {
				System.out.println("camera exception >>>>>>>>>>>>>>>>>>>>");
				e.printStackTrace();
			}
		} 
	}

	public String getPath(Uri uri) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(uri, filePathColumn, null,null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		return cursor.getString(columnIndex);
	}

	private void setUpViews() {
		homeButton = (Button) findViewById(R.id.edit_profile_home_btn);
		choosePicButton= (Button) findViewById(R.id.edit_profile_choose_pic_btn);
		updateProfileButton= (Button) findViewById(R.id.edit_profile_update_btn);
		fNameEditText= (EditText) findViewById(R.id.edit_profile_fname_edit);
		lNameEditText= (EditText) findViewById(R.id.edit_profile_lname_edit);
		phoneEditText= (EditText) findViewById(R.id.edit_profile_phone_edit);
		imageView=(ImageView)findViewById(R.id.edit_profile_pic_view);
		sosImageButton=(Button)findViewById(R.id.edit_profile_choose_sos_pic_btn);
		sosImageView=(ImageView)findViewById(R.id.edit_profile_sos_pic_view);
		sosLinearLayout=(LinearLayout)findViewById(R.id.edit_profile_sos_layout);
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
