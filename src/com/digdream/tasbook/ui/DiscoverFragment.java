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
		view = inflater.inflate(R.layout.fragment_discover, null);// ע�ⲻҪָ������ͼ
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
		viewFlow.setmSideBuffer(ids.length); // ʵ��ͼƬ������ �ҵ�ImageAdapterʵ��ͼƬ����Ϊ3

		indic = (CircleFlowIndicator) view.findViewById(R.id.viewflowindic);
		
		return view;
	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewFlow.setFlowIndicator(indic);
		viewFlow.setTimeSpan(2000);
		viewFlow.setSelection(3 * 1000); // ���ó�ʼλ��
		viewFlow.startAutoFlowTimer(); // �����Զ�����
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
	        //�ر�sso��Ȩ
	        oks.disableSSOWhenAuthorize();
	       
	        // ����ʱNotification��ͼ�������
	        oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
	        // title���⣬ӡ��ʼǡ����䡢��Ϣ��΢�š���������QQ�ռ�ʹ��
	        oks.setTitle(getString(R.string.share));
	        // titleUrl�Ǳ�����������ӣ�������������QQ�ռ�ʹ��
	        oks.setTitleUrl("http://sharesdk.cn");
	        // text�Ƿ����ı�������ƽ̨����Ҫ����ֶ�
	        oks.setText("���Ƿ����ı�");
	        // imagePath��ͼƬ�ı���·����Linked-In�����ƽ̨��֧�ִ˲���
	        oks.setImagePath("/sdcard/test.jpg");
	        // url����΢�ţ��������Ѻ�����Ȧ����ʹ��
	        oks.setUrl("http://sharesdk.cn");
	        // comment���Ҷ�������������ۣ�������������QQ�ռ�ʹ��
	        oks.setComment("���ǲ��������ı�");
	        // site�Ƿ�������ݵ���վ���ƣ�����QQ�ռ�ʹ��
	        oks.setSite(getString(R.string.app_name));
	        // siteUrl�Ƿ�������ݵ���վ��ַ������QQ�ռ�ʹ��
	        oks.setSiteUrl("http://sharesdk.cn");

	        // ��������GUI
	        oks.show(this.getActivity());
	   }
}