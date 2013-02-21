package com.iecho.main;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.iecho.apputil.ApplicationUtility;
import com.iecho.webservice.WebServiceConnection;

public class RegisterUserActivity extends Activity {
	private Button choosePicButton,registerUserButton,homeButton;
	private EditText fNameEditText,lNameEditText,emailEditText,phoneEditText,passwordEditText,rePasswordEditText;
	private ImageView profilePictureImageView;
	public Context context;
	private static final int CAMERA_REQUEST = 1888;
	private Bitmap bitmapImage;
	private byte[] byteArray;
	private ProgressDialog progressDialog;
	private Handler registerHandler;
	private WebServiceConnection serviceConnection;
	private HashMap<String, String> responseHashMap;
	private String status,statusMsg,userId;
	private String userRegisteredEmailId,password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_user);
		context=this;
		setUpViews();
		Button infoButton=(Button)findViewById(R.id.register_user_info);
		infoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent infoIntent=new Intent(context, AboutIechoActivity.class);
				startActivity(infoIntent);
			}
		});

		homeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		registerHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				try{
					if(progressDialog!=null){
						progressDialog.dismiss();
					}
					responseHashMap=serviceConnection.serviceResponce();
					if(responseHashMap!=null){
						status=responseHashMap.get("status");
						statusMsg=responseHashMap.get("statusMsg");
						userId=responseHashMap.get("userId");
						System.out.println("User id for registered user is :"+userId);
					}
					if(status.equals("0")){
						Toast.makeText(context, "Invalid user details."+statusMsg, 3000).show();
					}else{
						Toast.makeText(context, "User successfully registered.", 3000).show();
						Intent intent=new Intent();
						intent.putExtra("emailid", userRegisteredEmailId);
						intent.putExtra("password", password);
						setResult(1254, intent);
						finish();
					}
				}catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(context, "Server error", Toast.LENGTH_LONG).show();
				}
			}
		};



		registerUserButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(new ApplicationUtility(context).checkNetworkConnection()){
					final String fname=fNameEditText.getText().toString();
					final String lname=lNameEditText.getText().toString();
					final String email=emailEditText.getText().toString();
					final String phone=phoneEditText.getText().toString();
					final String password=passwordEditText.getText().toString();
					final String repassword=rePasswordEditText.getText().toString();
					if(fname.equals("")||lname.equals("")||email.equals("")||phone.equals("")||password.equals("")||repassword.equals("")){
						new ApplicationUtility(context,RegisterUserActivity.this).errorDialogAlertBox("Fill all required fields","Register error!",R.drawable.icon);
					}else{
						if (password.equals(repassword)) {
							//hide open keypad
							InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(emailEditText.getWindowToken(), 0);

							boolean valid=	android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

							if (valid) {
//								if (byteArray!=null) {
									progressDialog=ProgressDialog.show(context, "", "please wait...");
									userRegisteredEmailId=email;
									RegisterUserActivity.this.password=password;
									new Thread(){
										@Override
										public void run() {
											try{
												serviceConnection=new WebServiceConnection("REGISTER_USER", "http://tempuri.org/IiEchoMobileService/CustomerRegistration", "http://tempuri.org/", "CustomerRegistration", ApplicationUtility.SERVICE_URL, fname, lname, phone, email, repassword, byteArray);
												registerHandler.sendEmptyMessage(0);
											}catch (Exception e) {
												e.printStackTrace();
											}
										}
									}.start();
//								} else {
//									new ApplicationUtility(context,RegisterUserActivity.this).errorDialogAlertBox("Please choose a profile picture","Profile image error!",R.drawable.icon);									
//								}
							}else{
								new ApplicationUtility(context,RegisterUserActivity.this).errorDialogAlertBox("Email id is not valid","Email error!",R.drawable.icon);		
							}
						} else {
							new ApplicationUtility(context,RegisterUserActivity.this).errorDialogAlertBox("Password and Re-enter Password are not same","Password error!",R.drawable.icon);
						}
					}
				}else{
					Toast.makeText(context, "Please check network connection", Toast.LENGTH_LONG).show();
				}
			}
		});
		choosePicButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {


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

	}

	private void setUpViews(){
		choosePicButton=(Button)findViewById(R.id.register_user_pic_btn);
		registerUserButton=(Button)findViewById(R.id.register_user_register_btn);
		homeButton=(Button)findViewById(R.id.register_user_home_btn);
		fNameEditText=(EditText)findViewById(R.id.register_user_fname_edit);
		lNameEditText=(EditText)findViewById(R.id.register_user_lname_edit);
		emailEditText=(EditText)findViewById(R.id.register_user_email_edit);
		phoneEditText=(EditText)findViewById(R.id.register_user_phone_edit);
		passwordEditText=(EditText)findViewById(R.id.register_user_password_edit);
		rePasswordEditText=(EditText)findViewById(R.id.register_user_repassword_edit);
		profilePictureImageView=(ImageView)findViewById(R.id.register_user_pic_view);
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
				profilePictureImageView.setImageBitmap(bitmapImage);

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
				profilePictureImageView.setImageBitmap(bitmapImage);

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byteArray = stream.toByteArray();
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
}
