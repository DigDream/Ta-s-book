package com.digdream.tasbook.ui;

import java.util.Set;

import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.digdream.tasbook.R;
import com.digdream.tasbook.data.UserPreferences;
import com.digdream.tasbook.util.URLProtocol;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private String mName = "";
	private EditText mEmailView;
	private String mPassword = "";
	private EditText mPasswordView;
	private ProgressDialog mProgressDialog;
	private Button mSignInButton;
	private TextView mVersionCode;
	private TextView mVersionName;
	private Button mSignUpButton;
	private TextView mTitle;
	private Toast mToast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		findViewsByid();
		initView();

	}

	/**
	 * Toast
	 * 
	 * @param resId
	 */
	public void ShowToast(final int resId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mToast == null) {
					mToast = Toast.makeText(
							LoginActivity.this.getApplicationContext(), resId,
							Toast.LENGTH_LONG);
				} else {
					mToast.setText(resId);
				}
				mToast.show();
			}
		});
	}

	private void initView() {
		mSignUpButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						com.digdream.tasbook.ui.LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);
			}

		});
		mSignInButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mName = mEmailView.getText().toString();
				mPassword = mPasswordView.getText().toString();

				if (TextUtils.isEmpty(mName)) {
					ShowToast(R.string.toast_error_username_null);
					return;
				}

				if (TextUtils.isEmpty(mPassword)) {
					ShowToast(R.string.toast_error_password_null);
					return;
				}
				loginByAsyncHttpClientPost(mName, mPassword);

			}

		});
		new AlertDialog.Builder(com.digdream.tasbook.ui.LoginActivity.this)
				.setTitle("��ܰ��ʾ").setMessage("�ڱ��������ڼ䣬����ʹ���˺ź����붼Ϊtest�������ԡ�")
				.setPositiveButton("ȷ��", null).show();
		mTitle.setText("��¼");
	}

	private void findViewsByid() {
		mTitle = (TextView) findViewById(R.id.title);
		mEmailView = ((EditText) findViewById(R.id.login_email_edittext));
		mPasswordView = ((EditText) findViewById(R.id.login_password_edittext));
		mSignInButton = (Button) findViewById(R.id.login_sign_in_button);
		mSignUpButton = (Button) findViewById(R.id.login_create_account_button);
		mVersionName = ((TextView) findViewById(R.id.version_name));
		mVersionCode = ((TextView) findViewById(R.id.version_code));
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
		CookieStore cookieStore = new PersistentCookieStore(this);
		client.setCookieStore(cookieStore);
		HttpContext httpContext = client.getHttpContext();
		CookieStore cookies = (CookieStore) httpContext
				.getAttribute(ClientContext.COOKIE_STORE);
		if (cookies != null) {
			for (Cookie c : cookies.getCookies()) {
				Log.d("login activtity onsuccess before ~~" + c.getName(),
						c.getValue());
			}
		} else {
			Log.d("login activtity onsuccess  before~~", "cookies is null");
		}
		// ִ��post����
		client.post(URLProtocol.LOGIN_URL, params,
				new AsyncHttpResponseHandler() {
					private String json;

					/**
					 * �ɹ�����ķ��� statusCode:��Ӧ��״̬��; headers:��Ӧ��ͷ��Ϣ ���� ��Ӧ��ʱ�䣬��Ӧ�ķ�����
					 * ; responseBody:��Ӧ���ݵ��ֽ�
					 */
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						if (statusCode == 200) {
							UserPreferences preferences = new UserPreferences();
							preferences.init(LoginActivity.this);

							json = new String(responseBody);
							Log.i("tasbook", json);
							json.replaceAll("(\r\n|\r|\n|\n\r)", " ");
							try {
								JSONObject jsonobject = new JSONObject(json);
								int code = jsonobject.getInt("code");
								switch (code) {
								case URLProtocol.SUCCESS:
									preferences.saveName(mName);
									preferences.savePWD(mPassword);
									// preferences�����Զ���¼����
									preferences.setAutoLogin(true);
									ShowToast(R.string.toast_success_signin);
									JSONObject jsonresult = jsonobject.getJSONObject("result");
									// JSONObject addrJsonObj =
									// addrArrays.getJSONObject(i);

									String uid = jsonresult.getString("uid");
									String unickname = jsonresult
											.getString("unickname");
									String usign = jsonresult
											.getString("usign");
									String uhead_icon = jsonresult
											.getString("uhead_icon");
									preferences.saveUID(uid);
									preferences.saveNICKNAME(unickname);
									preferences.saveICON_HEAD(uhead_icon);
									preferences.saveSIGN(usign);
									Log.d("tasbooklogin", "code=" + code
											+ "uid=" + uid + "unickname="
											+ unickname + "usign=" + usign
											+ "uhead_icon" + uhead_icon);

									Intent intent = new Intent();
									intent.setClass(LoginActivity.this,
											MainTabActivity.class);
									startActivity(intent);
									finish();
									// String username =
									// jsonobject.getString("username");

									break;
								case URLProtocol.FALSE:
									int errorcode = jsonobject
											.getInt("errorcode");
									switch (errorcode) {
									case URLProtocol.ERROR_SIGNIN_FALSE:
										new AlertDialog.Builder(
												LoginActivity.this)
												.setTitle("��¼ʧ��")
												.setMessage("�û������������")
												.setPositiveButton("ȷ��", null)
												.show();
									case URLProtocol.ERROR_SIGNIN_LOCK:
										new AlertDialog.Builder(
												LoginActivity.this)
												.setTitle("��¼ʧ��")
												.setMessage("���û�������")
												.setPositiveButton("ȷ��", null)
												.show();
										break;

									}
									break;
								default:

								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

							// Intent intent = new Intent();
							// intent.setClass(LoginActivity.this,
							// MainTabActivity.class);
							// startActivity(intent);
							// finish();
						} else {
							ShowToast(R.string.toast_error_signin);
							System.out.println("test");
						}
					}

					/**
					 * ʧ�ܴ���ķ��� error����Ӧʧ�ܵĴ�����Ϣ��װ������쳣������
					 */
					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						ShowToast(R.string.toast_error_signin);
						error.printStackTrace();// �Ѵ�����Ϣ��ӡ���켣��
					}
				});
	}
}
