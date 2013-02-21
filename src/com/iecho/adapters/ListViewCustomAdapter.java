package com.iecho.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iecho.main.R;
import com.iecho.main.ViewFriendProfileActivity;

public class ListViewCustomAdapter extends BaseAdapter {
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

	public static class ViewHolder {
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
			holder.imgViewReject = (ImageView) convertView.findViewById(R.id.imgViewReject);
			holder.imgViewAccept = (ImageView) convertView.findViewById(R.id.imgViewAccept);
			holder.txtViewTitle = (TextView) convertView.findViewById(R.id.txtViewTitle);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.imgViewDetail.setImageResource(R.drawable.btn_arrow);
		holder.imgViewReject.setImageResource(R.drawable.btn_plus);
		holder.imgViewAccept.setImageResource(R.drawable.btn_cross);
		holder.txtViewTitle.setText(title[position]);

		holder.txtViewTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Toast.makeText(context, title[position], 3000).show();
			}
		});
		holder.imgViewDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Toast.makeText(context, title[position], 3000).show();
				Intent friendDetailIntent=new Intent(context,ViewFriendProfileActivity.class);
				context.startActivityForResult(friendDetailIntent, 555);
			}
		});
		holder.imgViewReject.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Toast.makeText(context, title[position], 3000).show();
			}
		});
		holder.imgViewAccept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Toast.makeText(context, title[position], 3000).show();
			}
		});

		return convertView;
	}

}
