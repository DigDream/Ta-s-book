package com.digdream.tasbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.digdream.tasbook.R;

public class ImageAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	AQuery aQuery;
	// private static final int[] ids = { R.drawable.test1, R.drawable.test2,
	// R.drawable.test3 };
	private String[] ids;
	

	public ImageAdapter(Context context) {
		mContext = context;
		aQuery = new AQuery(context);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public ImageAdapter(Context context, String[] ids) {
		mContext = context;
		aQuery = new AQuery(context);
		this.ids = ids;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE; // 返回最大值来实现循环
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.image_item, null);
		}

		aQuery.id(((ImageView) convertView.findViewById(R.id.imgView))).image(
				ids[position % ids.length]);

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Intent intent = new Intent(mContext,DetailActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putInt("image_id", ids[position%ids.length]);
				// intent.putExtras(bundle);
				// mContext.startActivity(intent);
				Toast.makeText(mContext, "点击了第" + (position%ids.length + 1) + "张图片",
						Toast.LENGTH_SHORT).show();

			}
		});
		return convertView;
	}

}
