package com.digdream.tasbook.ui;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.digdream.tasbook.R;
import com.digdream.tasbook.data.UserPreferences;
import com.digdream.tasbook.util.URLProtocol;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	// ��д�Ӷ���SDKӦ�ú�̨ע��õ���APPKEY
	private static String APPKEY = "32d36d1083b0";

	// ��д�Ӷ���SDKӦ�ú�̨ע��õ���APPSECRET
	private static String APPSECRET = "c886be90f4d031c4d21bdc796fd458a1";

	// ����ע�ᣬ�������ͷ��
	private static final String[] AVATARS = {
			"http://tupian.qqjay.com/u/2011/0729/e755c434c91fed9f6f73152731788cb3.jpg",
			"http://99touxiang.com/public/upload/nvsheng/125/27-011820_433.jpg",
			"http://img1.touxiang.cn/uploads/allimg/111029/2330264224-36.png",
			"http://img1.2345.com/duoteimg/qqTxImg/2012/04/09/13339485237265.jpg",
			"http://diy.qqjay.com/u/files/2012/0523/f466c38e1c6c99ee2d6cd7746207a97a.jpg",
			"http://img1.touxiang.cn/uploads/20121224/24-054837_708.jpg",
			"http://img1.touxiang.cn/uploads/20121212/12-060125_658.jpg",
			"http://img1.touxiang.cn/uploads/20130608/08-054059_703.jpg",
			"http://diy.qqjay.com/u2/2013/0422/fadc08459b1ef5fc1ea6b5b8d22e44b4.jpg",
			"http://img1.2345.com/duoteimg/qqTxImg/2012/04/09/13339510584349.jpg",
			"http://img1.touxiang.cn/uploads/20130515/15-080722_514.jpg",
			"http://diy.qqjay.com/u2/2013/0401/4355c29b30d295b26da6f242a65bcaad.jpg" };

	private boolean ready;
	private Dialog pd;
	private TextView tvNum;

	private TextView mTitle;
	private EditText mFullName;
	private EditText mPhone;
	private EditText mPassword;
	private EditText mConfirmPwd;
	private ImageView mBtnBack;

	private String sName;

	private String sPassword;

	private String sPhone;

	private Button mBtnRegister;

	public static Handler mhandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		findViewsById();
		initView();
	}

	/**
	 * Toast
	 * 
	 * @param resId
	 */
	public void ShowToast(final int resId) {
		runOnUiThread(new Runnable() {

			private Toast mToast;

			@Override
			public void run() {
				if (mToast == null) {
					mToast = Toast.makeText(
							RegisterActivity.this.getApplicationContext(),
							resId, Toast.LENGTH_LONG);
				} else {
					mToast.setText(resId);
				}
				mToast.show();
			}
		});
	}

	private void register() {
		sName = mFullName.getText().toString();
		sPassword = mPassword.getText().toString();
		String sConfirmPassword = mConfirmPwd.getText().toString();
		sPhone = mPhone.getText().toString();
		if (TextUtils.isEmpty(sName)) {
			ShowToast(R.string.toast_error_username_null);
			return;
		}

		if (TextUtils.isEmpty(sPassword)) {
			ShowToast(R.string.toast_error_password_null);
			return;
		}
		if (TextUtils.isEmpty(sPhone)) {
			ShowToast(R.string.toast_error_phone_null);
			return;
		}
		if (TextUtils.isEmpty(sConfirmPassword)) {
			ShowToast(R.string.toast_error_password_error);
			return;
		}
		if (TextUtils.equals(sPassword.trim(), sConfirmPassword.trim()) == false) {
			ShowToast(R.string.toast_error_password_error);
			return;
		}

		/*
		 * mhandler = new Handler() {
		 * 
		 * 
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 * 
		 * @Override public void handleMessage(Message msg) { if (msg.what ==
		 * URLProtocol.CMD_REGISTER) { Bundle bundle = msg.getData(); int code =
		 * bundle.getInt("code"); switch (code) { // ������ע��ɹ�����Ϣ��ע��ɹ�toast����ת��
		 * case URLProtocol.ERROR_SIGNUP_SUCCESS:
		 * ShowToast(R.string.toast_success_sign_up); Intent intent = new
		 * Intent();
		 * 
		 * intent.setClass(RegisterActivity.this, MainTabActivity.class);
		 * 
		 * startActivity(intent); finish(); break; case
		 * URLProtocol.ERROR_SIGNUP_NAME_REPEAT: new
		 * AlertDialog.Builder(RegisterActivity.this) .setTitle("ע��ʧ��")
		 * .setMessage("�û����Ѵ��ڣ�������û���") .setPositiveButton("ȷ��", null) .show();
		 * break; case URLProtocol.ERROR_SIGNUP_PHONE_REPEAT: new
		 * AlertDialog.Builder(RegisterActivity.this) .setTitle("ע��ʧ��")
		 * .setMessage("�ֻ����Ѵ��ڣ�������ֻ���") .setPositiveButton("ȷ��", null) .show();
		 * break; case URLProtocol.ERROR_SIGNUP_DATABASE_FALSE: new
		 * AlertDialog.Builder(RegisterActivity.this) .setTitle("ע��ʧ��")
		 * .setMessage("���ݿ����ʧ��") .setPositiveButton("ȷ��", null) .show(); break;
		 * default:
		 * 
		 * } } } };
		 */
		RegisterByAsyncHttpClientPost(sName, sPassword, sPhone);

	}

	private void initView() {
		mTitle.setText("ע��");
		mBtnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});
		mBtnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				register();
			}

		});
	}

	private void findViewsById() {
		mTitle = (TextView) findViewById(R.id.title);
		mFullName = (EditText) findViewById(R.id.signup_fullname_edittext);
		mPhone = (EditText) findViewById(R.id.signup_phone_edittext);
		mConfirmPwd = (EditText) findViewById(R.id.signup_password_confirm_edittext);
		mPassword = (EditText) findViewById(R.id.signup_password_edittext);
		mBtnBack = (ImageView) findViewById(R.id.btn_action_bar_left);
		mBtnRegister = (Button) findViewById(R.id.signup_signup_button);
	}

	/**
	 * ����AsyncHttpClient��Post��ʽ����ʵ��
	 * 
	 * @param userName
	 * @param userPass
	 * @param phone
	 */
	public void RegisterByAsyncHttpClientPost(String userName, String userPass,
			String phone) {
		AsyncHttpClient client = new AsyncHttpClient(); // �����첽����Ŀͻ��˶���
		// ������������ķ�װ�Ķ���
		RequestParams params = new RequestParams();
		params.put("user_name", userName); // ��������Ĳ������Ͳ���ֵ
		params.put("user_pass", userPass);// ��������Ĳ������Ͳ���
		params.put("phone", phone);
		// ִ��post����
		client.post(URLProtocol.ROOT, params, new AsyncHttpResponseHandler() {
			private String json;

			/**
			 * �ɹ�����ķ��� statusCode:��Ӧ��״̬��; headers:��Ӧ��ͷ��Ϣ ���� ��Ӧ��ʱ�䣬��Ӧ�ķ����� ;
			 * responseBody:��Ӧ���ݵ��ֽ�
			 */
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				if (statusCode == 200) {
					/*
					 * UserPreferences preferences = new UserPreferences();
					 * preferences.init(LoginActivity.this);
					 * preferences.saveName(name);
					 * preferences.savePWD(password); // preferences�����Զ���¼����
					 * preferences.setAutoLogin(true);
					 * 
					 * ShowToast(R.string.login_success); Intent intent = new
					 * Intent(); intent.setClass(LoginActivity.this,
					 * ListViewActivity.class); startActivity(intent); finish();
					 */
					json = new String(responseBody);
					Log.i("tasbook", json);
					json.replaceAll("(\r\n|\r|\n|\n\r)", " ");
					try {
						JSONObject jsonobject = new JSONObject(json);
						int code = jsonobject.getInt("code");
						switch (code) {
						case URLProtocol.SUCCESS:
							ShowToast(R.string.toast_success_sign_up);
							Intent intent = new Intent();
							// JSONArray addrArrays = jsonobject
							// .getJSONArray("result");
							/*
							 * for (int i = 0; i < addrArrays.length(); i++) {
							 * JSONObject addrJsonObj = addrArrays
							 * .getJSONObject(i);
							 */
							JSONObject jsonresult = jsonobject.getJSONObject("result");
							String uid = jsonresult.getString("uid");
							String username = jsonresult.getString("username");
							UserPreferences preferences = new UserPreferences();
							preferences.init(RegisterActivity.this);
							preferences.saveName(username);
							preferences.saveUID(uid);

							intent.setClass(RegisterActivity.this,
									MainTabActivity.class);

							startActivity(intent);
							finish();
							break;
						case URLProtocol.FALSE:
							int errorcode = jsonobject.getInt("errorcode");
							switch (errorcode) {
							case URLProtocol.ERROR_SIGNUP_NAME_REPEAT:
								new AlertDialog.Builder(RegisterActivity.this)
										.setTitle("ע��ʧ��")
										.setMessage("�û����Ѵ��ڣ�������û���")
										.setPositiveButton("ȷ��", null).show();
							case URLProtocol.ERROR_SIGNUP_PHONE_REPEAT:
								new AlertDialog.Builder(RegisterActivity.this)
										.setTitle("ע��ʧ��")
										.setMessage("�ֻ����Ѵ��ڣ�������ֻ���")
										.setPositiveButton("ȷ��", null).show();
								break;
							case URLProtocol.ERROR_SIGNUP_DATABASE_FALSE:
								new AlertDialog.Builder(RegisterActivity.this)
										.setTitle("ע��ʧ��").setMessage("���ݿ����ʧ��")
										.setPositiveButton("ȷ��", null).show();
								break;
							}

							break;

						default:

						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					ShowToast(R.string.toast_error_signup);
				}
			}

			/**
			 * ʧ�ܴ���ķ��� error����Ӧʧ�ܵĴ�����Ϣ��װ������쳣������
			 */
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				ShowToast(R.string.network_not_connected);
				error.printStackTrace();// �Ѵ�����Ϣ��ӡ���켣��
			}
		});

	}

}
