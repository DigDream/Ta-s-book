package com.digdream.tasbook.ui;

import java.util.Set;

import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.protocol.HttpContext;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.smssdk.SMSSDK;
import cn.waps.AppConnect;

import com.baidu.mapapi.SDKInitializer;
import com.digdream.tasbook.R;
import com.digdream.tasbook.UpdateManager;
import com.digdream.tasbook.data.UserPreferences;
import com.digdream.tasbook.util.ExampleUtil;
import com.digdream.tasbook.util.URLProtocol;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
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
	public static boolean isForeground = false;
	public static boolean isupdate = false;
	private UpdateManager manager;
	private static final String TAG = "JPush";
	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAGS = 1002;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final View view = View.inflate(this, R.layout.activity_splash, null);
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());  
		// ȥ����������ȫ��
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(view);
		String alias = "guodong";
		if (TextUtils.isEmpty(alias)) {
			Toast.makeText(MainActivity.this, R.string.error_alias_empty,
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (!ExampleUtil.isValidTagAndAlias(alias)) {
			Toast.makeText(MainActivity.this, R.string.error_tag_gs_empty,
					Toast.LENGTH_SHORT).show();
			return;
		}
		registerMessageReceiver(); // used for receive msg

		mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
		AppConnect.getInstance("1bda048b5c34181525053016086a4ef5","Google",this); 
		SMSSDK.initSDK(this, "32d36d1083b0", "c886be90f4d031c4d21bdc796fd458a1");
		AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(2000);
		view.startAnimation(animation);

		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation arg0) {
				// ���ﵭ�붯�������󣬽���sd��������ļ�⣬�°汾�������Գ�ʼ����
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// sd card ����
					isNetworkAlertDialog();
				} else {
					// ��ǰSD ��������
					new AlertDialog.Builder(
							com.digdream.tasbook.ui.MainActivity.this)
							.setCancelable(false)
							.setTitle("��ܰ��ʾ")
							.setMessage("δ����SD������ȷ��Ҫ������?")
							.setPositiveButton("ȷ��",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											// ��sdcard���ò�����ͬ
											isNetworkAlertDialog();
										}
									})
							.setNegativeButton("ȡ��",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											finish();
											com.digdream.tasbook.ui.MainActivity.this
													.stopService(getIntent());
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
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_SET_ALIAS:
				Log.d(TAG, "Set alias in handler.");
				JPushInterface.setAliasAndTags(getApplicationContext(),
						(String) msg.obj, null, mAliasCallback);
				break;

			case MSG_SET_TAGS:
				Log.d(TAG, "Set tags in handler.");
				JPushInterface.setAliasAndTags(getApplicationContext(), null,
						(Set<String>) msg.obj, mTagsCallback);
				break;

			default:
				Log.i(TAG, "Unhandled msg - " + msg.what);
			}
		}
	};
	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs;
			switch (code) {
			case 0:
				logs = "Set tag and alias success";
				Log.i(TAG, logs);
				break;

			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				Log.i(TAG, logs);
				if (ExampleUtil.isConnected(getApplicationContext())) {
					mHandler.sendMessageDelayed(
							mHandler.obtainMessage(MSG_SET_ALIAS, alias),
							1000 * 60);
				} else {
					Log.i(TAG, "No network");
				}
				break;

			default:
				logs = "Failed with errorCode = " + code;
				Log.e(TAG, logs);
			}

			ExampleUtil.showToast(logs, getApplicationContext());
		}

	};
	private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs;
			switch (code) {
			case 0:
				logs = "Set tag and alias success";
				Log.i(TAG, logs);
				break;

			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				Log.i(TAG, logs);
				if (ExampleUtil.isConnected(getApplicationContext())) {
					mHandler.sendMessageDelayed(
							mHandler.obtainMessage(MSG_SET_TAGS, tags),
							1000 * 60);
				} else {
					Log.i(TAG, "No network");
				}
				break;

			default:
				logs = "Failed with errorCode = " + code;
				Log.e(TAG, logs);
			}

			ExampleUtil.showToast(logs, getApplicationContext());
		}

	};

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
	//	Intent intentservice = new Intent(this, ParseJsonDataService.class);
		//startService(intentservice);
		if (label == 1) {
			intent.setClass(MainActivity.this, SwitchActivity.class);
		} else if (label == 2) {
			//�ж�sp���Ƿ��б�����������Ϣ���Զ���¼�Ƿ�Ϊtrue
			UserPreferences preferences = new UserPreferences();
			preferences.init(MainActivity.this);
			if (preferences.getAutoLogin()) {
				String autoname = preferences.getName();
				String autopassword = preferences.getPWD();
				//�����Զ���¼����ȡ��session��Ϣ
				loginByAsyncHttpClientPost(autoname,autopassword);
				intent.setClass(MainActivity.this, MainTabActivity.class);
				startActivity(intent);
				finish();
			} else {
				intent.setClass(MainActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		}
		
		startActivity(intent);
		finish();
	}
	/**
	 * ����AsyncHttpClient��Post��ʽ����ʵ��
	 * 
	 * @param userName
	 * @param userPass
	 */
	public void loginByAsyncHttpClientPost(String userName, String userPass) {
		AsyncHttpClient client = new AsyncHttpClient(); // �����첽����Ŀͻ��˶���
		// ������������ķ�װ�Ķ���
		RequestParams params = new RequestParams();
		params.put("user_name", userName); // ��������Ĳ������Ͳ���ֵ
		params.put("user_pass", userPass);// ��������Ĳ������Ͳ���
		// ִ��post����
	    CookieStore cookieStore = new PersistentCookieStore(this);  
        client.setCookieStore(cookieStore);  
        HttpContext httpContext = client.getHttpContext();  
        CookieStore cookies = (CookieStore) httpContext.getAttribute(ClientContext.COOKIE_STORE);  
        if(cookies!=null){  
            for(Cookie c:cookies.getCookies()){  
                Log.d("login activtity onsuccess before ~~"+c.getName(),c.getValue());  
            }  
        }else{  
            Log.d("login activtity onsuccess  before~~","cookies is null");  
        }  
		client.post(URLProtocol.LOGIN_URL, params, new AsyncHttpResponseHandler() {
			/**
			 * �ɹ�����ķ��� statusCode:��Ӧ��״̬��; headers:��Ӧ��ͷ��Ϣ ���� ��Ӧ��ʱ�䣬��Ӧ�ķ����� ;
			 * responseBody:��Ӧ���ݵ��ֽ�
			 */
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				
			}
			/**
			 * ʧ�ܴ���ķ��� error����Ӧʧ�ܵĴ�����Ϣ��װ������쳣������
			 */
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				
			}
		});
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
		if (isNetworkAvailable(com.digdream.tasbook.ui.MainActivity.this) == true) {
			Toast.makeText(MainActivity.this, "��������", Toast.LENGTH_LONG).show();
			manager = new UpdateManager(MainActivity.this);
			// ����������
			manager.check();
			if (manager.update == false) {
				isFirsted();
			} else if (manager.updatefalse == false) {
				Toast.makeText(MainActivity.this, "�������ȡ������Ϣʧ��",
						Toast.LENGTH_LONG).show();
				isFirsted();
			} else {
				if(isupdate == true){
				new AlertDialog.Builder(
						com.digdream.tasbook.ui.MainActivity.this)
						.setCancelable(false)
						.setTitle("��ܰ��ʾ")
						.setMessage(R.string.soft_update_info)
						.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										manager.showDownloadDialog();
									}
								})
						.setNegativeButton("ȡ��",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										isFirsted();
									}
								}).show();
				}
				else{
					isFirsted();
				}
			}
			// ��תActivity���ж��Ƿ��һ������
		} else {
			new AlertDialog.Builder(com.digdream.tasbook.ui.MainActivity.this)
					.setCancelable(false)
					.setTitle("��ܰ��ʾ")
					.setMessage("δ�������磬��ȷ��Ҫ������?")
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									isFirsted();
								}
							})
					.setNegativeButton("��������",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									startNetworkSettingActivity(com.digdream.tasbook.ui.MainActivity.this);
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
					"com.android.settings.WirelessSettings");// android4.0ϵͳ�Ҳ�����activity��
		}
		context.startActivity(intent);
	}

	public void isFirsted() {
		SharedPreferences sharedPreferences = getSharedPreferences("share",
				MODE_PRIVATE);
		final boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun",
				true);
		final Editor editor = sharedPreferences.edit();
		if (isFirstRun) {
			toNextActivity(1);
			editor.putBoolean("isFirstRun", false);
			editor.commit();
		} else {
			toNextActivity(2);
		}
	}
	// ��ʼ�� JPush������Ѿ���ʼ������û�е�¼�ɹ�����ִ�����µ�¼��
	private void init() {
		JPushInterface.init(getApplicationContext());
	}

	@Override
	protected void onResume() {
		isForeground = true;
		super.onResume();
	}

	@Override
	protected void onPause() {
		isForeground = false;
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}

	// for receive customer msg from jpush server
	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.digdream.tasbook.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";

	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				String messge = intent.getStringExtra(KEY_MESSAGE);
				String extras = intent.getStringExtra(KEY_EXTRAS);
				StringBuilder showMsg = new StringBuilder();
				showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
				if (!ExampleUtil.isEmpty(extras)) {
					showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
				}
				setCostomMsg(showMsg.toString());
			}
		}
	}

	private void setCostomMsg(String msg) {

	}
}
