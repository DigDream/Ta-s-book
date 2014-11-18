package com.digdream.tasbook.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.digdream.tasbook.R;
import com.digdream.tasbook.adapter.ImageAdapter;
import com.digdream.tasbook.view.CircleFlowIndicator;
import com.digdream.tasbook.view.MyImageView;
import com.digdream.tasbook.view.ViewFlow;

public class DiscoverFragment extends Fragment {
	/*ViewPager viewpager;
	int ads[] = { R.drawable.view_add_1, R.drawable.view_add_2,
			R.drawable.view_add_3, R.drawable.view_add_4 };
	List<View> items;
	ImageView image, dot, dots[];
	LinearLayout viewGroup;
	int currentIndex = 0;
	Boolean f = true;*/
	private Button but = null;
	private View view;
	
	private ViewFlow viewFlow;
	private static final int[] ids = { R.drawable.test1, R.drawable.test2,
			R.drawable.test3, R.drawable.ic_launcher };

	private String[] urls = {
			"http://182.92.180.94/Tasbook/discover/1.jpg",
			"http://182.92.180.94/Tasbook/discover/2.jpg",
			"http://182.92.180.94/Tasbook/discover/3.jpg",
			"http://182.92.180.94/Tasbook/discover/4.jpg" };
	private CircleFlowIndicator indic;
	private MyImageView joke;
	private MyImageView ad;
	private MyImageView c_idea;
	private MyImageView c_constellation;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_discover, null);// 注意不要指定父视图
		/*initViewPager();
		initDot();
		
		new Thread(new Runnable() {  
            @Override  
            public void run() {  
                while (true) {  
                    if (f) {  
                        viewHandler.sendEmptyMessage(atomicInteger.get());  
                        atomicOption();  
                    }  
                }  
            }  
        }).start();
		*/
		viewFlow = (ViewFlow) view.findViewById(R.id.viewflow);
		viewFlow.setAdapter(new ImageAdapter(this.getActivity(), urls));
		viewFlow.setmSideBuffer(ids.length); // 实际图片张数， 我的ImageAdapter实际图片张数为3

		indic = (CircleFlowIndicator) view.findViewById(R.id.viewflowindic);
		
		return view;
	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewFlow.setFlowIndicator(indic);
		viewFlow.setTimeSpan(2000);
		viewFlow.setSelection(3 * 1000); // 设置初始位置
		viewFlow.startAutoFlowTimer(); // 启动自动播放
		joke = (MyImageView) view.findViewById(R.id.c_joke);
		joke.setOnClickIntent(new MyImageView.OnViewClick() {

			@Override
			public void onClick() {
				Intent intent = new Intent(com.digdream.tasbook.ui.DiscoverFragment.this.getActivity(), MapActivity.class);
		        startActivity(intent);
			}
		});
		ad = (MyImageView)view.findViewById(R.id.iv_ad);
		ad.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(com.digdream.tasbook.ui.DiscoverFragment.this.getActivity(), AdActivity.class);
		        startActivity(intent);				
			}
			
		});
		c_idea = (MyImageView)view.findViewById(R.id.c_idea);
		c_idea.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(com.digdream.tasbook.ui.DiscoverFragment.this.getActivity(), LocationActivity.class);
		        startActivity(intent);				
			}
			
		});
		c_constellation = (MyImageView)view.findViewById(R.id.c_constellation);
		c_constellation.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				showShare();	
			}
			
		});
	}
	 private void showShare() {
	        ShareSDK.initSDK(this.getActivity(),"32d36d1083b0");
	        OnekeyShare oks = new OnekeyShare();
	        //关闭sso授权
	        oks.disableSSOWhenAuthorize();
	       
	        // 分享时Notification的图标和文字
	        oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
	        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
	        oks.setTitle(getString(R.string.share));
	        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
	        oks.setTitleUrl("http://sharesdk.cn");
	        // text是分享文本，所有平台都需要这个字段
	        oks.setText("我是分享文本");
	        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
	        oks.setImagePath("/sdcard/test.jpg");
	        // url仅在微信（包括好友和朋友圈）中使用
	        oks.setUrl("http://sharesdk.cn");
	        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
	        oks.setComment("我是测试评论文本");
	        // site是分享此内容的网站名称，仅在QQ空间使用
	        oks.setSite(getString(R.string.app_name));
	        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
	        oks.setSiteUrl("http://sharesdk.cn");

	        // 启动分享GUI
	        oks.show(this.getActivity());
	   }
}