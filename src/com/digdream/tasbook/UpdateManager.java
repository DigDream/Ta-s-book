package com.digdream.tasbook;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.xmlpull.v1.XmlPullParser;

import com.digdream.tasbook.bean.UpdateInfo;
import com.digdream.tasbook.ui.MainActivity;
import com.digdream.tasbook.ui.MainTabActivity;
import com.digdream.tasbook.util.IsbnXmlSax;
import com.digdream.tasbook.util.URLProtocol;
import com.digdream.tasbook.util.UpdateParseXmlService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * @author coolszy
 * @date 2012-4-26
 * @blog http://blog.92coding.com
 */

public class UpdateManager {
	/* ������ */
	private static final int DOWNLOAD = 1;
	/* ���ؽ��� */
	private static final int DOWNLOAD_FINISH = 2;
	/* ���������XML��Ϣ */
	HashMap<String, String> mHashMap;
	/* ���ر���·�� */
	private String mSavePath;
	/* ��¼���������� */
	private int progress;
	/* �Ƿ�ȡ������ */
	public boolean cancelUpdate = false;
	public boolean update = true;
	private String Name;
	private Context mContext;
	/* ���½����� */
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	private String DownloadUrl;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// ��������
			case DOWNLOAD:
				// ���ý�����λ��
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// ��װ�ļ�
				installApk();
				break;
			default:
				break;
			}
		};
	};
	private InputStream inputStream;
	private Integer serviceCode;
	public boolean updatefalse = true;

	public UpdateManager(Context context) {
		this.mContext = context;
	}

	/**
	 * ����������
	 */
	public void checkUpdate() {
		if (isUpdate()) {
			// ��ʾ��ʾ�Ի���
			showNoticeDialog();
		} else {
			Toast.makeText(mContext, R.string.soft_update_no, Toast.LENGTH_LONG)
					.show();
		}
	}

	public UpdateInfo getUpdataInfo(InputStream is) throws Exception {

		UpdateInfo info = null;
		XmlPullParser parser = Xml.newPullParser();
		if(is == null){
			//�������ȡ��ʱ
			updatefalse = false;
		}
		parser.setInput(is, "GB2312");// ���ý���������Դ�������ʽ
		int event = parser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT: // ��ʼ����
				// ���ڴ�����ʼ����ع���
				info = new UpdateInfo();
				Log.i("UpdatePullParser", "--START_DOCUMENT--");
				break;
			case XmlPullParser.START_TAG:
				Log.i("UpdatePullParser", "--START_TAG--");
				String tag = parser.getName();
				if ("version".equals(tag)) {
					info.setVersion(new Integer(parser.nextText())); // ��ȡ�汾��
				}  else if ("name".equals(tag)) {
					info.setName(parser.nextText()); // ��ȡname
				}else if ("url".equals(tag)) {
					info.setUrl(parser.nextText()); // ��ȡurl��ַ
				}
				break;
			case XmlPullParser.END_TAG:// ����һ��Ԫ�أ����ж��Ԫ�أ�����������
				break;
			default:
				break;
			}
			event = parser.next();
		}
		return info; // ����һ��UpdataInfoʵ��
	}

	public InputStream getXml() throws Exception {
		String TAG = "URLConnect";

		HttpURLConnection conn = (HttpURLConnection) new URL(URLProtocol.queryString)
				.openConnection();
		conn.setReadTimeout(5 * 1000); // �������ӳ�ʱ��ʱ��
		// conn.setRequestMethod("GET");
		conn.connect(); // ��ʼ����
		if (conn.getResponseCode() == 200) {
			InputStream is = conn.getInputStream();
			return is; // ����InputStream
		} else {
			Log.e(TAG, "---����ʧ��---");
		}
		conn.disconnect(); // �Ͽ�����
		return null;
	}

	
	public void check() {

		new Thread() {
			

			@Override
			public void run() {
				// ��Ҫ���߳�ִ�еķ���
				try {
					InputStream is = getXml(); // ��ȡxml����
					UpdateInfo info = getUpdataInfo(is); // ���ý�������
					serviceCode = info.getVersion(); // ��÷������汾
					Name = info.getName();
					Log.i("test",
							"check--infoVersion=" + info.getVersion()
									+ "infoURL=" + info.getUrl() +"name=" + info.getName());
					DownloadUrl = info.getUrl();
				} catch (Exception e) {
					e.printStackTrace();
				}
				// ��handlerf����һ����Ϣ
				mHandlerf.sendEmptyMessage(new Message().what = 1);
			}
		}.start();

	}

	// Handler��Ϣ���ջ���
	private Handler mHandlerf = new Handler() {
		// Handler���յ���Ӧ��Ϣ����ˢ��ui�Ȳ���
		public void handleMessage(Message msg) {
			int versionCode = getVersionCode(mContext);
			super.handleMessage(msg);
			if (msg.what == 1) {
				// �յ���Ϣ���ڴ˽���ui��ز������罫������������ʾ������

				System.out.println(serviceCode);
				// �汾�ж�
				if (serviceCode > versionCode) {
					update = true;
					com.digdream.tasbook.ui.MainActivity.isupdate = true;
				} else {
					Toast.makeText(mContext, R.string.soft_update_no,
							Toast.LENGTH_LONG).show();
					update = false;
				}
			}
		}

	};

	/**
	 * �������Ƿ��и��°汾
	 * 
	 * @return
	 */
	private boolean isUpdate() {
		int versionCode = getVersionCode(mContext);
		AsyncHttpClient client = new AsyncHttpClient();
		try {

			client.get(URLProtocol.queryString, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] responseBody) {

					// ��ȡ��ǰ����汾

					InputStream inputStream = new ByteArrayInputStream(
							responseBody);

					UpdateParseXmlService service = new UpdateParseXmlService();
					try {
						mHashMap = service.parseXml(inputStream);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// ��version.xml�ŵ������ϣ�Ȼ���ȡ�ļ���Ϣ
					// InputStream inStream =
					// UpdateParseXmlService.class.getClassLoader().getResourceAsStream("version.xml");
					// ����XML�ļ��� ����XML�ļ��Ƚ�С�����ʹ��DOM��ʽ���н���
					if (null != mHashMap) {
						serviceCode = Integer.valueOf(mHashMap.get("version"));
					}

				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {

				}
			});
		} catch (Exception e) {
		}

		System.out.println(serviceCode);
		// �汾�ж�
		if (serviceCode > versionCode) {
			return true;
		}

		return false;

	}

	/**
	 * ��ȡ����汾��
	 * 
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			// ��ȡ����汾�ţ���ӦAndroidManifest.xml��android:versionCode
			versionCode = context.getPackageManager().getPackageInfo(
					"com.digdream.tasbook", 0).versionCode;
			System.out.println("versioncode"+versionCode);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * ��ʾ������¶Ի���
	 */
	private void showNoticeDialog() {
		// ����Ի���
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_update_title);
		builder.setMessage(R.string.soft_update_info);
		// ����
		builder.setPositiveButton(R.string.soft_update_updatebtn,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// ��ʾ���ضԻ���
						showDownloadDialog();
					}
				});
		// �Ժ����
		builder.setNegativeButton(R.string.soft_update_later,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						cancelUpdate = true;
						update = false;
					}
				});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}

	/**
	 * ��ʾ������ضԻ���
	 */
	public void showDownloadDialog() {
		// ����������ضԻ���
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_updating);
		// �����ضԻ������ӽ�����
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		// ȡ������
		builder.setNegativeButton(R.string.soft_update_cancel,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// ����ȡ��״̬
						
					}
				});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		// �����ļ�
		downloadApk();
	}

	/**
	 * ����apk�ļ�
	 */
	private void downloadApk() {
		// �������߳��������
		new downloadApkThread().start();
	}

	/**
	 * �����ļ��߳�
	 * 
	 * @author coolszy
	 * @date 2012-4-26
	 * @blog http://blog.92coding.com
	 */
	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				// �ж�SD���Ƿ���ڣ������Ƿ���ж�дȨ��
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// ��ô洢����·��
					String sdpath = Environment.getExternalStorageDirectory()
							+ "/";
					mSavePath = sdpath + "download";
					URL url = new URL(DownloadUrl);
					// ��������
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();
					// ��ȡ�ļ���С
					int length = conn.getContentLength();
					// ����������
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// �ж��ļ�Ŀ¼�Ƿ����
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(mSavePath, Name);
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// ����
					byte buf[] = new byte[1024];
					// д�뵽�ļ���
					do {
						int numread = is.read(buf);
						count += numread;
						// ���������λ��
						progress = (int) (((float) count / length) * 100);
						// ���½���
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {
							// �������
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// д���ļ�
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// ���ȡ����ֹͣ����.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// ȡ�����ضԻ�����ʾ
			mDownloadDialog.dismiss();
		}
	};

	/**
	 * ��װAPK�ļ�
	 */
	private void installApk() {
		File apkfile = new File(mSavePath, Name);
		if (!apkfile.exists()) {
			return;
		}
		// ͨ��Intent��װAPK�ļ�
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
}
