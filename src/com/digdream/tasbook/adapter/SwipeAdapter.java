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
import com.digdream.tasbook.entity.WXMessage;
import com.digdream.tasbook.interfaces.OnImageDownload;

public class SwipeAdapter extends BaseAdapter {
    /**
     * 上下文对象
     */
    private Context mContext = null;
    private List<WXMessage> data;
	private ImageDownloader mDownloader;
	private String url;
	public static final ImageCache IMAGE_CACHE = CacheManager.getImageCache();
    /**
     * @param mainActivity
     */
    public SwipeAdapter(Context ctx,List<WXMessage> data) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.bookitems_list, parent, false);
            holder = new ViewHolder();
            holder.item_left = (RelativeLayout)convertView.findViewById(R.id.item_left);
            holder.item_right = (RelativeLayout)convertView.findViewById(R.id.item_right);
            
            holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tv_title = (TextView)convertView.findViewById(R.id.tv_title);
            holder.tv_msg = (TextView)convertView.findViewById(R.id.tv_msg);
            holder.tv_time = (TextView)convertView.findViewById(R.id.tv_time);
            
            holder.item_right_txt = (TextView)convertView.findViewById(R.id.item_right_txt);
            convertView.setTag(holder);
        } else {// 有直接获得ViewHolder
            holder = (ViewHolder)convertView.getTag();
        }
        
        Log.i("SwipeAdapter", "getView position="+position);
        
        WXMessage msg = data.get(position);
        
        holder.tv_title.setText(msg.getTitle());
        holder.tv_msg.setText(msg.getMsg());
        holder.tv_time.setText(msg.getTime());
        url = msg.getIcon_id();
        //holder.iv_icon.setImageResource(msg.getIcon_id());
        if (mDownloader == null) {  
            mDownloader = new ImageDownloader();  
        }  
        final ListView mListView = (ListView) convertView.findViewById(R.id.mListView); 
        //这句代码的作用是为了解决convertView被重用的时候，图片预设的问题   
        holder.iv_icon.setImageResource(R.drawable.qq_icon);  
        holder.iv_icon.setTag(url);
        IMAGE_CACHE.get(url, holder.iv_icon);
        /*if (mDownloader != null) {  
            //异步下载图片   
            mDownloader.imageDownload(url, holder.iv_icon, "/tasbook",(Activity) mContext, new OnImageDownload() {  
                        @Override  
                        public void onDownloadSucc(Bitmap bitmap,  
                                String c_url,ImageView mimageView) {  
                            ImageView imageView = (ImageView) mListView.findViewWithTag(c_url);  
                            if (imageView != null) {  
                            	imageView.setImageBitmap(bitmap);  
                                imageView.setTag("");  
                            }   
                        }  
                    });  
        }  */
        holder.item_right.setOnClickListener(new OnClickListener() {
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
    	RelativeLayout item_left;
    	RelativeLayout item_right;

        TextView tv_title;
        TextView tv_msg;
        TextView tv_time;
        ImageView iv_icon;

        TextView item_right_txt;
    }
    
    /**
     * 单击事件监听器
     */
    private onRightItemClickListener mListener = null;
    
    public void setOnRightItemClickListener(onRightItemClickListener listener){
    	mListener = listener;
    }

    public interface onRightItemClickListener {
        void onRightItemClick(View v, int position);
    }
}
