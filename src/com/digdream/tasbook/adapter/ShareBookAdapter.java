package com.digdream.tasbook.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.trinea.android.common.service.impl.ImageCache;
import cn.trinea.android.common.util.CacheManager;

import com.digdream.tasbook.R;
import com.digdream.tasbook.bean.ImageDownloader;
import com.digdream.tasbook.entity.BookMessage;
import com.digdream.tasbook.interfaces.OnImageDownload;

public class ShareBookAdapter extends BaseAdapter {
	/**
	 * 上下文对象
	 */
	private Context mContext = null;
	public List<BookMessage> data;
	private ImageDownloader mDownloader;
	private String url;

	//public static final ImageCache IMAGE_CACHE = CacheManager.getImageCache();

	/**
	 * @param mainActivity
	 */
	public ShareBookAdapter(Context ctx, List<BookMessage> data) {
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
					R.layout.sharebook_items_list, parent, false);
			holder = new ViewHolder();
			holder.booklist = (RelativeLayout) convertView
					.findViewById(R.id.booklist);

			holder.iv_bookimage = (ImageView) convertView
					.findViewById(R.id.iv_bookimage);
			holder.iv_head_icon = (ImageView) convertView
					.findViewById(R.id.iv_head_icon);
			holder.iv_comment = (ImageView) convertView
					.findViewById(R.id.iv_comment);
			holder.iv_more = (ImageView) convertView.findViewById(R.id.iv_more);
			holder.iv_like = (ImageView) convertView.findViewById(R.id.iv_like);

			holder.tv_book_share_time = (TextView) convertView
					.findViewById(R.id.tv_book_share_time);
			holder.tv_share_author = (TextView) convertView
					.findViewById(R.id.tv_share_author);
			holder.tv_book_summary = (TextView) convertView
					.findViewById(R.id.tv_book_summary);
			holder.tv_book_title = (TextView) convertView
					.findViewById(R.id.tv_book_title);
			holder.tv_book_tag = (TextView) convertView
					.findViewById(R.id.tv_book_tag);

			convertView.setTag(holder);
		} else {// 有直接获得ViewHolder
			holder = (ViewHolder) convertView.getTag();
		}

		Log.i("SharebookAdapter", "getView position=" + position);

		BookMessage msg = data.get(position);

		holder.tv_book_title.setText(msg.getTitle());
		holder.tv_book_summary.setText(msg.getMsg());
		holder.tv_book_share_time.setText(msg.getTime());
		holder.tv_book_tag.setText(msg.getTag());
		holder.tv_share_author.setText(msg.getShareAuthor());
		holder.iv_comment
				.setImageResource(R.drawable.read_bottom_progress_comment);
		holder.iv_more.setImageResource(R.drawable.btn_followed_article);
		holder.iv_like.setImageResource(R.drawable.vote_icon_useful);
		holder.iv_head_icon.setImageResource(R.drawable.username_icon);
		url = msg.getIcon_id();
		// holder.iv_icon.setImageResource(msg.getIcon_id());
		if (mDownloader == null) {
			mDownloader = new ImageDownloader();
		}
		final ListView mListView = (ListView) convertView
				.findViewById(R.id.share_book_list_view);
		// 这句代码的作用是为了解决convertView被重用的时候，图片预设的问题
		holder.iv_bookimage.setImageResource(R.drawable.qq_icon);
		holder.iv_bookimage.setTag(url);
		 //IMAGE_CACHE.get(url, holder.iv_bookimage);

		if (mDownloader != null) {
			// 异步下载图片
			mDownloader.imageDownload(url, holder.iv_bookimage,
					"/tasbook/bookimage", (Activity) mContext,
					new OnImageDownload() {
						@Override
						public void onDownloadSucc(Bitmap bitmap, String c_url,
								ImageView mimageView) {
							ImageView imageView = (ImageView) mListView
									.findViewWithTag(c_url);
							if (imageView != null) {
								imageView.setImageBitmap(bitmap);
								imageView.setTag("");
							}
						}
					});
		}
		holder.iv_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onRightItemClick(v, position);
				}
			}
		});
		return convertView;
	}

	static class ViewHolder {
		RelativeLayout booklist;
		TextView tv_book_share_time;
		TextView tv_share_author;
		TextView tv_book_summary;
		TextView tv_book_title;
		TextView tv_book_tag;

		ImageView iv_bookimage;
		ImageView iv_head_icon;
		ImageView iv_comment;
		ImageView iv_more;
		ImageView iv_like;

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
