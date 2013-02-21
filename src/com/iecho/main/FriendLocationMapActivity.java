package com.iecho.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.iecho.apputil.ApplicationUtility;
import com.iecho.apputil.CommonFunctions;
import com.iecho.database.DataBaseConnectionManager;
import com.iecho.webservice.WebServiceConnection;

public class FriendLocationMapActivity extends MapActivity {
	private MyLocationOverlayExtension mMyLocationOverlay;
	private MapController mMapController;
	private GeoPoint point;
	private Intent intent;
	private String friendId = "";
	private ProgressDialog progressDialog;
	private Context context;
	private Handler handler, loadingMapHandler;
	private Thread thread;
	private WebServiceConnection serviceConnection;
	private double latitude = 32.802955; // 28.979312;
	private double longitude = -96.769923; // 77.717285;
	private HashMap<String, String> hashMap;
	private String status = "", lat = "", longi = "";
	private MapView mapView;
	private boolean flag = true;
	private String firstName = "";
	private String lastName = "";
	private String countryName;
	private String addressLine;
	private static String[] groupArray;
	private DataBaseConnectionManager connectionManager;
	private ArrayList list;
	private ArrayList<String> list2;
	private int totalContacts = 0;
	private ListViewCustomAdapter listViewCustomAdapter;
	private ListView contactListView;
	public static Handler loadContactHandler;
	private ProgressBar progressBar;
	private ArrayList<Bitmap> friendImagesArrayList;
	private ArrayList<String> friendsIDArrayList = new ArrayList<String>();
	private ArrayList<String> idsArrayList = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.friend_location_mapview);
		progressDialog = ProgressDialog.show(context, "", "please wait...");
		mapView = (MapView) findViewById(R.id.mapview);
		contactListView = (ListView) findViewById(R.id.list_friendsmaps);
		progressBar = (ProgressBar)findViewById(R.id.list_progressbar);

		try {
			intent = getIntent();
			Bundle bundle = intent.getExtras();
			friendId = bundle.getString("friendId");
			firstName = bundle.getString("fisrtName");
			lastName = bundle.getString("lastName");
		} catch (Exception e) {
			e.printStackTrace();
		}

		connectionManager = new DataBaseConnectionManager(context);

		thread=new Thread(){
			@Override
			public void run() {
				while(flag){
					serviceConnection=new WebServiceConnection("GET_FRIEND_UPDATED_LOCATION", "http://tempuri.org/IiEchoMobileService/GetFindMeTrackingInformation", "http://tempuri.org/", "GetFindMeTrackingInformation", ApplicationUtility.SERVICE_URL,"customerId",friendId);
					handler.sendEmptyMessage(0);
					try {
						sleep(30000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				try {
					hashMap = serviceConnection.serviceResponce();
					status = hashMap.get("status");
					System.out.println("status>>" + status);
					if (status.equals("true")) {
						lat = hashMap.get("latitude");
						longi = hashMap.get("longitude");
						latitude = Double.parseDouble(lat);
						longitude = Double.parseDouble(longi);
						getLocationDetails();
					} else {

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if (progressDialog != null) {
						progressDialog.dismiss();
						loadingMapHandler.sendEmptyMessage(0);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};


		loadingMapHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				try {
					mapView.setBuiltInZoomControls(true);
					mMyLocationOverlay = new MyLocationOverlayExtension(context, mapView);

					mapView.getOverlays().add(mMyLocationOverlay);
					mapView.postInvalidate();
					mMapController = mapView.getController();
					mMapController.setZoom(16);

					mMyLocationOverlay.runOnFirstFix(new Runnable() {
						@Override
						public void run() {
							mMapController.animateTo(mMyLocationOverlay.getMyLocation());
							mMapController.setCenter(mMyLocationOverlay.getMyLocation());
						}
					});

					mMyLocationOverlay.enableMyLocation();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		loadContactList();

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "All");
		menu.add(0, 2, 0, "Private");
		menu.add(0, 3, 0, "Public");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			idsArrayList.clear();
			for (int i = 0; i < GetAllUsersCurrentState.hashMap.size(); i++) {
				if (GetAllUsersCurrentState.hashMap.get(friendsIDArrayList.get(i)).equals("Public")||GetAllUsersCurrentState.hashMap.get(friendsIDArrayList.get(i)).equals("Private")) {
					idsArrayList.add(friendsIDArrayList.get(i));
				}
			}
			System.out.println("size =========== "+idsArrayList.size());
			showSpecificFriendsResults(idsArrayList);
			break;
		case 2:
			idsArrayList.clear();
			for (int i = 0; i < GetAllUsersCurrentState.hashMap.size(); i++) {
				if (GetAllUsersCurrentState.hashMap.get(friendsIDArrayList.get(i)).equals("Private")) {
					idsArrayList.add(friendsIDArrayList.get(i));
				}
			}
			System.out.println("size =========== "+idsArrayList.size());
			showSpecificFriendsResults(idsArrayList);
			break;

		case 3:
			idsArrayList.clear();
			for (int i = 0; i < GetAllUsersCurrentState.hashMap.size(); i++) {
				if (GetAllUsersCurrentState.hashMap.get(friendsIDArrayList.get(i)).equals("Public")) {
					idsArrayList.add(friendsIDArrayList.get(i));
				}
			}
			System.out.println("size =========== "+idsArrayList.size());
			showSpecificFriendsResults(idsArrayList);
			break;

		default:
			break;
		}
		return true;
	}



	private void getLocationDetails() {
		Geocoder geocoder = new Geocoder(getApplicationContext());
		try {
			List<Address> fromLocation = geocoder.getFromLocation(latitude, longitude, 1);
			if (fromLocation != null) {
				addressLine = fromLocation.get(0).getAddressLine(0);
				countryName = fromLocation.get(0).getCountryName();
				System.out.println("countryName : " + countryName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		flag = false;
		try {
			System.out.println("Flag >>>>>> " + flag);
			mMyLocationOverlay.disableMyLocation();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private class MyLocationOverlayExtension extends MyLocationOverlay {

		public MyLocationOverlayExtension(Context context, MapView mapView) {

			super(context, mapView);
		}
		@Override
		public GeoPoint getMyLocation() {
			GeoPoint point = new GeoPoint((int)(latitude * 1E6), (int)(longitude * 1E6));
			System.out.println(">>>>>>>>>>>>>>>> overrided getMyLocation() >>>>>>>>>>>>>>>>>>>>");
			return point;
		}


		@Override
		public synchronized void onLocationChanged(Location location) {
			location.setLatitude(latitude);
			location.setLongitude(longitude);
			point = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
			System.out.println("Latitude is >>>>>>>>>>>>>>>> "+(int) (location.getLatitude() * 1E6));
			System.out.println("Longitude is >>>>>>>>>>>>>>>> "+(int) (location.getLongitude() * 1E6));
			mMapController.animateTo(point);
			mMapController.setCenter(point);
		}

		Point screenPts;

		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,long when) {
			super.draw(canvas, mapView, shadow);
			screenPts = new Point();
			mapView.getProjection().toPixels(new GeoPoint((int) (latitude * 1E6),(int) (longitude * 1E6)), screenPts);
			Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.pushpin);
			canvas.drawBitmap(bmp, screenPts.x, screenPts.y - 50, null);
			return true;
		}
	}

	private void showSpecificFriendsResults(ArrayList<String> friendsIDsArrayList) {
		if (connectionManager == null) {
			connectionManager = new DataBaseConnectionManager(context);
		}
		String[] contactNamesArray = new String[friendsIDsArrayList.size()];
		for (int i = 0; i < friendsIDsArrayList.size(); i++) {
			contactNamesArray[i] = connectionManager.getFriendDetails(friendsIDsArrayList.get(i));
			friendImagesArrayList = new ArrayList<Bitmap>();

			for (int j = 0; j < friendsIDsArrayList.size(); j++) {
				Bitmap contactImageArray=connectionManager.getImage(friendsIDsArrayList.get(j).toString());
				friendImagesArrayList.add(contactImageArray);
			}
		}
		listViewCustomAdapter = new ListViewCustomAdapter(FriendLocationMapActivity.this, contactNamesArray,GetAllUserLocations.friendLocationInfoArray, friendImagesArrayList,friendsIDsArrayList);
		contactListView.setAdapter(listViewCustomAdapter);
	}

	private void loadContactList() {
		if (connectionManager == null) {
			connectionManager = new DataBaseConnectionManager(context);
		}
		String[] idsStrings = connectionManager.getAllUsersId();
		String[] contactNamesArray = new String[idsStrings.length];
		friendImagesArrayList = new ArrayList<Bitmap>();
		for (int i = 0; i < idsStrings.length; i++) {
			friendsIDArrayList.add(idsStrings[i]);
			contactNamesArray[i] = connectionManager.getFriendDetails(idsStrings[i]);
			Bitmap contactImageArray = connectionManager.getImage(idsStrings[i]);
			friendImagesArrayList.add(contactImageArray);
		}

		if (GetAllUserLocations.friendLocationInfoArray == null) {
			while (GetAllUserLocations.friendLocationInfoArray == null) {
				listViewCustomAdapter = new ListViewCustomAdapter(FriendLocationMapActivity.this, contactNamesArray, GetAllUserLocations.friendLocationInfoArray, friendImagesArrayList,friendsIDArrayList);
				contactListView.setAdapter(listViewCustomAdapter);
				progressBar.setVisibility(View.INVISIBLE);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			listViewCustomAdapter = new ListViewCustomAdapter(FriendLocationMapActivity.this, contactNamesArray, GetAllUserLocations.friendLocationInfoArray, friendImagesArrayList,friendsIDArrayList);
			contactListView.setAdapter(listViewCustomAdapter);
			progressBar.setVisibility(View.INVISIBLE);
		}
	}


	//****************************** inner adapter class starts ******************************************

	class ListViewCustomAdapter extends BaseAdapter {
		public String title[], userAddress[];
		public ArrayList<Bitmap> description;
		public Activity context;
		public LayoutInflater inflater;
		public ArrayList<String> friendIDArrayList;

		public ListViewCustomAdapter(Activity context, String[] title,String[] userAddress, ArrayList<Bitmap> image,ArrayList<String> idsArrayList) {
			super();
			this.context = context;
			this.title = title;
			this.description = image;
			this.userAddress = userAddress;
			this.friendIDArrayList = idsArrayList;
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

		class ViewHolder {
			ImageView imgViewLogo;
			ImageView arrowImgViewLogo;
			TextView txtViewTitle;
			TextView userLocationTextView;
			RelativeLayout relativeLayout;
		}

		ViewHolder holder;

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.contacts_friends_list_row, null);
				holder.imgViewLogo = (ImageView) convertView.findViewById(R.id.imgViewLogo);
				holder.txtViewTitle = (TextView) convertView.findViewById(R.id.txtViewTitle);
				holder.arrowImgViewLogo = (ImageView) convertView.findViewById(R.id.imgViewLogo_arrow);
				holder.relativeLayout = (RelativeLayout)convertView.findViewById(R.id.relativeLayout1);
				holder.userLocationTextView = (TextView) convertView.findViewById(R.id.txtViewTitle_bottom);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			holder.txtViewTitle.setText(title[position]);
			holder.imgViewLogo.setImageBitmap(description.get(position));
			holder.userLocationTextView.setText(userAddress[position]);

			holder.arrowImgViewLogo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CommonFunctions.getInstance(FriendLocationMapActivity.this, context).showDialog(title[position], userAddress[position], GetAllUsersCurrentState.hashMap.get(friendIDArrayList.get(position)));
				}
			});

			holder.imgViewLogo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});

			holder.txtViewTitle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});
			return convertView;
		}
	}
	//****************************** inner adapter class ends ******************************************
}