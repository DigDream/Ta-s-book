package com.digdream.tasbook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final View view = View.inflate(this, R.layout.activity_splash, null);
		super.onCreate(savedInstanceState);
		// 去掉标题栏和全屏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(view);

		AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(2000);
		view.startAnimation(animation);

		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation arg0) {
				// 这里淡入动画结束后，进行sd卡，网络的检测，下版本加入语言初始化、
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// sd card 可用
					isNetworkAlertDialog();
				} else {
					// 当前SD 卡不可用
					new AlertDialog.Builder(
							com.digdream.tasbook.MainActivity.this)
							.setCancelable(false)
							.setTitle("温馨提示")
							.setMessage("未发现SD卡，您确定要继续吗?")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											//与sdcard可用部分相同
											isNetworkAlertDialog();
										}
									})
							.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											finish();
											com.digdream.tasbook.MainActivity.this.stopService(getIntent());
											System.exit(0);
										}
									}).show();
				}
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {

			}

			@Override
			public void onAnimationStart(Animation arg0) {

			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void toNextActivity(int label) {
		Intent intent = new Intent();
		if (label == 1) {
			//intent.setClass(MainActivity.this,SwitchActivity.class);
		} else if(label == 2){
			//intent.setClass(MainActivity.this, MainTabActivity.class);
		}
		startActivity(intent);
		finish();
	}

	public boolean isNetworkAvailable(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public void isNetworkAlertDialog() {
		if (isNetworkAvailable(com.digdream.tasbook.MainActivity.this) == true) {
			Toast.makeText(MainActivity.this, "存在网络", Toast.LENGTH_LONG).show();
			//跳转Activity，判断是否第一次运行
			isFirsted();
		} else {
			new AlertDialog.Builder(com.digdream.tasbook.MainActivity.this)
					.setCancelable(false)
					.setTitle("温馨提示")
					.setMessage("未发现网络，您确定要继续吗?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									isFirsted();
								}
							})
					.setNegativeButton("设置网络",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									startNetworkSettingActivity(com.digdream.tasbook.MainActivity.this);
								}
							}).show();

		}
	}

	public static void startNetworkSettingActivity(Context context) {
		Intent intent = new Intent();
		final int sdkVersion = VERSION.SDK_INT;
		if (sdkVersion >= 11) {
			intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
		} else {
			intent.setClassName("com.android.settings",
					"com.android.settings.WirelessSettings");// android4.0系统找不到此activity。
		}
		context.startActivity(intent);
	}
	
	public void isFirsted(){
		SharedPreferences sharedPreferences = getSharedPreferences("share", MODE_PRIVATE);
		final boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
		final Editor editor = sharedPreferences.edit();
		if (isFirstRun)
		{
			toNextActivity(1);
			editor.putBoolean("isFirstRun", false);
			editor.commit();
		} 
		else
		{
			toNextActivity(2);
		} 
	}

}
