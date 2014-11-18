package com.digdream.tasbook;

import io.rong.imkit.RongIM;

import org.apache.http.client.CookieStore;

import cn.jpush.android.api.JPushInterface;
import android.app.Application;
import android.util.Log;

public class TasbookApplication extends Application {
	private static final String TAG = "JPush";

    /* (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d(TAG, "[ExampleApplication] onCreate");
		RongIM.init(this, "pwe86ga5erp66", R.drawable.ic_launcher);

        JPushInterface.setDebugMode(true); 	// ���ÿ�����־,����ʱ��ر���־
        JPushInterface.init(this);     		// ��ʼ�� JPush
	}
	private CookieStore cookies;  
    public CookieStore getCookie(){   
        return cookies;
    }
    public void setCookie(CookieStore cks){
        cookies = cks;
    }
}