package com.digdream.tasbook.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.digdream.tasbook.R;
import com.digdream.tasbook.entity.NoteMessage;

public class NotesAdapter extends BaseAdapter {
	/**
	 * 上下文对象
	 */
	private Context mContext = null;
	public List<NoteMessage> data;

	// private ImageDownloader mDownloader;
	// private String url;

	// public static final ImageCache IMAGE_CACHE =
	// CacheManager.getImageCache();

	/**
	 * @param mainActivity
	 */
	public NotesAdapter(Context ctx, List<NoteMessage> data) {
		mContext = ctx;
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.note_item_list, parent, false);
			holder = new ViewHolder();

			holder.note_listitem_flag = (ImageView) convertView
					.findViewById(R.id.note_listitem_flag);

			holder.note_listitem_title = (TextView) convertView
					.findViewById(R.id.note_listitem_title);
			holder.note_listitem_author = (TextView) convertView
					.findViewById(R.id.note_listitem_author);
			holder.note_listitem_date = (TextView) convertView
					.findViewById(R.id.note_listitem_date);
			holder.note_listitem_commentCount = (TextView) convertView
					.findViewById(R.id.note_listitem_commentCount);

			convertView.setTag(holder);
		} else {// 有直接获得ViewHolder
			holder = (ViewHolder) convertView.getTag();
		}

		Log.i("SharebookAdapter", "getView position=" + position);

		NoteMessage msg = data.get(position);
		holder.note_listitem_title.setText(msg.getTitle());
		holder.note_listitem_commentCount.setText(String.valueOf(msg.getCommentCount()));
		holder.note_listitem_date.setText(msg.getTime());
		holder.note_listitem_author.setText(msg.getShareAuthor());
		holder.note_listitem_flag
				.setImageResource(R.drawable.widget_today_icon);
		// url = msg.getIcon_id();
		// holder.iv_icon.setImageResource(msg.getIcon_id());
		/*
		 * if (mDownloader == null) { mDownloader = new ImageDownloader(); }
		 * final ListView mListView = (ListView) convertView
		 * .findViewById(R.id.share_book_list_view);
		 */
		// 这句代码的作用是为了解决convertView被重用的时候，图片预设的问题
		// holder.iv_bookimage.setImageResource(R.drawable.qq_icon);
		// holder.iv_bookimage.setTag(url);
		// IMAGE_CACHE.get(url, holder.iv_bookimage);

		/*
		 * if (mDownloader != null) { // 异步下载图片 mDownloader.imageDownload(url,
		 * holder.iv_bookimage, "/tasbook/bookimage", (Activity) mContext, new
		 * OnImageDownload() {
		 * 
		 * @Override public void onDownloadSucc(Bitmap bitmap, String c_url,
		 * ImageView mimageView) { ImageView imageView = (ImageView) mListView
		 * .findViewWithTag(c_url); if (imageView != null) {
		 * imageView.setImageBitmap(bitmap); imageView.setTag(""); } } }); }
		 */
		return convertView;
	}

	static class ViewHolder {
		TextView note_listitem_title;
		TextView note_listitem_author;
		TextView note_listitem_date;
		TextView note_listitem_commentCount;
		ImageView note_listitem_flag;

	}

	/**
	 * 单击事件监听器
	 */
	private onRightItemClickListener mListener = null;

	public void setOnRightItemClickListener(onRightItemClickListener listener) {
		mListener = listener;
	}

	public interface onRightItemClickListener {
		void onRightItemClick(View v, int position);
	}
}
