package com.digdream.tasbook.ui;

import cn.waps.AppConnect;

import com.digdream.tasbook.R;
import com.digdream.tasbook.data.UserPreferences;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainTabActivity extends FragmentActivity implements
		OnClickListener {

	private ViewPager viewPager;// 页卡内容
	private ImageView imageView;// 动画图片
	// private TextView textView1, textView2, textView3;
	// private List<View> views;// Tab页面列表
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	// private View view1, view2, view3;// 各个页卡
	SlidePagerAdapter mPagerAdapter;
	private Button bt1, bt2, bt3, bt4, bt5, bt6;
	private TextView mTitle;
	private ImageView btn_action_bar_left;
	private SlidingMenu menu;
	private Button btn_user_setting;
	private TextView tv_unickname;
	private TextView tv_usign;
	private Button btn_user_logout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_maintab);
		// InitImageView();
		// InitTextView();
		mPagerAdapter = new SlidePagerAdapter(this.getSupportFragmentManager());
		InitViewPager();

		/*** 初始化侧滑菜单 Begin ***/
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		// menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		// menu.setShadowWidthRes(R.dimen.shadow_width); // 1）
		// menu.setShadowDrawable(R.drawable.shadow); // 2）
		// menu.setBehindOffsetRes(R.dimen.slidingmenu_offset); // 3）
		// menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.user_items); // 4）
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeEnabled(false);
		menu.setBehindScrollScale(0.25f);
		menu.setFadeDegree(0.25f);

		// 配置背景图片
		menu.setBackgroundImage(R.drawable.img_frame_background);
		// 设置专场动画效果
		menu.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (percentOpen * 0.25 + 0.75);
				canvas.scale(scale, scale, -canvas.getWidth() / 2,
						canvas.getHeight() / 2);
			}
		});

		menu.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (1 - percentOpen * 0.25);
				canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
			}
		});
		/*** 初始化侧滑菜单 End ***/
		initItemsView();
	}

	private void initItemsView() {
		bt1 = (Button) findViewById(R.id.bt1);
		bt2 = (Button) findViewById(R.id.bt2);
		bt3 = (Button) findViewById(R.id.bt3);
		/*
		 * bt4 = (Button) findViewById(R.id.bt4); bt5 = (Button)
		 * findViewById(R.id.bt5); bt6 = (Button) findViewById(R.id.bt6);
		 */
		btn_user_setting = (Button) findViewById(R.id.btn_user_setting);
		btn_user_setting.setOnClickListener(this);
		btn_user_logout = (Button) findViewById(R.id.btn_user_logout);
		btn_user_logout.setOnClickListener(this);
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("寻找那些丢失在记忆里的书");
		bt1.setOnClickListener(this);
		bt2.setOnClickListener(this);
		bt3.setOnClickListener(this);
		/*
		 * bt4.setOnClickListener(this); bt5.setOnClickListener(this);
		 * bt6.setOnClickListener(this);
		 */
		btn_action_bar_left = (ImageView) findViewById(R.id.btn_action_bar_left);
		btn_action_bar_left.setOnClickListener(this);
		tv_unickname = (TextView) findViewById(R.id.tv_unickname);
		tv_usign = (TextView) findViewById(R.id.tv_usign);
		userInfo();
	}

	private void userInfo() {
		UserPreferences preferences = new UserPreferences();
		preferences.init(MainTabActivity.this);
		tv_unickname.setText("Test测试");
		tv_usign.setText("我是测试签名。。。。");
		// tv_unickname.setText(preferences.getNICKNAME());
		// tv_usign.setText(preferences.getSIGN());
		Log.d("tasbookmaintab",
				preferences.getNICKNAME() + preferences.getSIGN());
	}

	private void InitViewPager() {
		viewPager = (ViewPager) findViewById(R.id.vPager);
		// views = new ArrayList<View>();
		LayoutInflater inflater = getLayoutInflater();
		/*
		 * view1=inflater.inflate(R.layout.lay1, null);
		 * view2=inflater.inflate(R.layout.lay2, null);
		 * view3=inflater.inflate(R.layout.lay3, null); views.add(view1);
		 * views.add(view2); views.add(view3);
		 */
		viewPager.setAdapter(mPagerAdapter);
		// viewPager.setAdapter(new MyViewPagerAdapter(views));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	/* PagerAdapter class */
	public class SlidePagerAdapter extends FragmentPagerAdapter {
		public SlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			/*
			 * IMPORTANT: This is the point. We create a RootFragment acting as
			 * a container for other fragments
			 */
			Fragment fragment = null;
			switch (position) {
			case 0:
				fragment = new FindBookFragment();
				break;
			case 1:
				fragment = new NotesFragment();
				fragment.setHasOptionsMenu(true);
				break;
			case 2:
				fragment = new DiscoverFragment();
				break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}
	}

	/**
	 * 初始化头标
	 */

	/*
	 * private void InitTextView() { textView1 = (TextView)
	 * findViewById(R.id.text1); textView2 = (TextView)
	 * findViewById(R.id.text2); textView3 = (TextView)
	 * findViewById(R.id.text3);
	 * 
	 * textView1.setOnClickListener(new MyOnClickListener(0));
	 * textView2.setOnClickListener(new MyOnClickListener(1));
	 * textView3.setOnClickListener(new MyOnClickListener(2)); }
	 */

	/**
	 * 2 * 初始化动画，这个就是页卡滑动时，下面的横线也滑动的效果，在这里需要计算一些数据 3
	 */

	/*
	 * private void InitImageView() { imageView = (ImageView)
	 * findViewById(R.id.cursor); bmpW =
	 * BitmapFactory.decodeResource(getResources(), R.drawable.a) .getWidth();//
	 * 获取图片宽度 DisplayMetrics dm = new DisplayMetrics();
	 * getWindowManager().getDefaultDisplay().getMetrics(dm); int screenW =
	 * dm.widthPixels;// 获取分辨率宽度 offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
	 * Matrix matrix = new Matrix(); matrix.postTranslate(offset, 0);
	 * imageView.setImageMatrix(matrix);// 设置动画初始位置 }
	 */

	/**
	 * 
	 * 头标点击监听 3
	 */
	/*
	 * private class MyOnClickListener implements OnClickListener { private int
	 * index = 0;
	 * 
	 * public MyOnClickListener(int i) { index = i; }
	 * 
	 * public void onClick(View v) { viewPager.setCurrentItem(index); }
	 * 
	 * }
	 */

	public class MyViewPagerAdapter extends PagerAdapter {
		private List<View> mListViews;

		public MyViewPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageSelected(int arg0) {
			/*
			 * 两种方法，这个是一种，下面还有一种，显然这个比较麻烦 Animation animation = null; switch
			 * (arg0) { case 0: if (currIndex == 1) { animation = new
			 * TranslateAnimation(one, 0, 0, 0); } else if (currIndex == 2) {
			 * animation = new TranslateAnimation(two, 0, 0, 0); } break; case
			 * 1: if (currIndex == 0) { animation = new
			 * TranslateAnimation(offset, one, 0, 0); } else if (currIndex == 2)
			 * { animation = new TranslateAnimation(two, one, 0, 0); } break;
			 * case 2: if (currIndex == 0) { animation = new
			 * TranslateAnimation(offset, two, 0, 0); } else if (currIndex == 1)
			 * { animation = new TranslateAnimation(one, two, 0, 0); } break;
			 * 
			 * }
			 */
			Animation animation = new TranslateAnimation(one * currIndex, one
					* arg0, 0, 0);// 显然这个比较简洁，只有一行代码。
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			// imageView.startAnimation(animation);
			switch (viewPager.getCurrentItem()) {
			case 0:
				mTitle.setText("寻找那些丢失在记忆里的书");
				break;
			case 1:
				mTitle.setText("随手记那些记忆里的事与物");
				break;
			case 2:
				mTitle.setText("发现那些曾经迷失的东西");
				break;

			}

			Toast.makeText(MainTabActivity.this,
					"您选择了" + viewPager.getCurrentItem() + "页卡",
					Toast.LENGTH_SHORT).show();

		}

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this)
					.setCancelable(false)
					.setTitle("温馨提示")
					.setMessage("您确定要退出吗?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
									AppConnect
											.getInstance(MainTabActivity.this)
											.close();
									System.exit(0);
								}

							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).show();
			return true;
			// 不知道返回true或是false有什么区别??
		}

		return super.dispatchKeyEvent(event);

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.bt1:

			intent.setClass(MainTabActivity.this, MyBooksActivity.class);
			startActivity(intent);
			break;

		case R.id.bt2:

			Toast.makeText(MainTabActivity.this,
					"功能正在测试中",
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.bt3:
			intent.setClass(MainTabActivity.this, MyNotesActivity.class);
			startActivity(intent);

			break;
		/*
		 * case R.id.bt4: Toast.makeText(MainTabActivity.this, "您选择了4" +
		 * viewPager.getCurrentItem() + "页卡", Toast.LENGTH_SHORT).show();
		 * 
		 * break; case R.id.bt5: Toast.makeText(MainTabActivity.this, "您选择了5" +
		 * viewPager.getCurrentItem() + "页卡", Toast.LENGTH_SHORT).show();
		 * 
		 * break; case R.id.bt6: Toast.makeText(MainTabActivity.this, "您选择了6" +
		 * viewPager.getCurrentItem() + "页卡", Toast.LENGTH_SHORT).show();
		 * 
		 * break;
		 */
		case R.id.btn_action_bar_left:
			menu.showMenu();
			break;
		case R.id.btn_user_setting:

			intent.setClass(MainTabActivity.this, SettingActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_user_logout:

			UserPreferences preferences = new UserPreferences();
			preferences.init(MainTabActivity.this);
			preferences.setAutoLogin(false);

			intent.setClass(MainTabActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add("menu");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menufalse) {

		menu.showMenu();
		return false;// 返回为true 则显示系统menu
	}
}