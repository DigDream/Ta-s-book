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
	/* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	/* 保存解析的XML信息 */
	HashMap<String, String> mHashMap;
	/* 下载保存路径 */
	private String mSavePath;
	/* 记录进度条数量 */
	private int progress;
	/* 是否取消更新 */
	public boolean cancelUpdate = false;
	public boolean update = true;
	private String Name;
	private Context mContext;
	/* 更新进度条 */
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	private String DownloadUrl;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 正在下载
			case DOWNLOAD:
				// 设置进度条位置
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// 安装文件
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
	 * 检测软件更新
	 */
	public void checkUpdate() {
		if (isUpdate()) {
			// 显示提示对话框
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
			//从网络读取超时
			updatefalse = false;
		}
		parser.setInput(is, "GB2312");// 设置解析的数据源，编码格式
		int event = parser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT: // 开始解析
				// 可在此做初始化相关工作
				info = new UpdateInfo();
				Log.i("UpdatePullParser", "--START_DOCUMENT--");
				break;
			case XmlPullParser.START_TAG:
				Log.i("UpdatePullParser", "--START_TAG--");
				String tag = parser.getName();
				if ("version".equals(tag)) {
					info.setVersion(new Integer(parser.nextText())); // 获取版本号
				}  else if ("name".equals(tag)) {
					info.setName(parser.nextText()); // 获取name
				}else if ("url".equals(tag)) {
					info.setUrl(parser.nextText()); // 获取url地址
				}
				break;
			case XmlPullParser.END_TAG:// 读完一个元素，如有多个元素，存入容器中
				break;
			default:
				break;
			}
			event = parser.next();
		}
		return info; // 返回一个UpdataInfo实体
	}

	public InputStream getXml() throws Exception {
		String TAG = "URLConnect";

		HttpURLConnection conn = (HttpURLConnection) new URL(URLProtocol.queryString)
				.openConnection();
		conn.setReadTimeout(5 * 1000); // 设置连接超时的时间
		// conn.setRequestMethod("GET");
		conn.connect(); // 开始连接
		if (conn.getResponseCode() == 200) {
			InputStream is = conn.getInputStream();
			return is; // 返回InputStream
		} else {
			Log.e(TAG, "---连接失败---");
		}
		conn.disconnect(); // 断开连接
		return null;
	}

	
	public void check() {

		new Thread() {
			

			@Override
			public void run() {
				// 需要在线程执行的方法
				try {
					InputStream is = getXml(); // 获取xml内容
					UpdateInfo info = getUpdataInfo(is); // 调用解析方法
					serviceCode = info.getVersion(); // 获得服务器版本
					Name = info.getName();
					Log.i("test",
							"check--infoVersion=" + info.getVersion()
									+ "infoURL=" + info.getUrl() +"name=" + info.getName());
					DownloadUrl = info.getUrl();
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 给handlerf发送一个消息
				mHandlerf.sendEmptyMessage(new Message().what = 1);
			}
		}.start();

	}

	// Handler消息接收机制
	private Handler mHandlerf = new Handler() {
		// Handler接收到相应消息进行刷新ui等操作
		public void handleMessage(Message msg) {
			int versionCode = getVersionCode(mContext);
			super.handleMessage(msg);
			if (msg.what == 1) {
				// 收到消息，在此进行ui相关操作，如将解析的内容显示出来。

				System.out.println(serviceCode);
				// 版本判断
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
	 * 检查软件是否有更新版本
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

					// 获取当前软件版本

					InputStream inputStream = new ByteArrayInputStream(
							responseBody);

					UpdateParseXmlService service = new UpdateParseXmlService();
					try {
						mHashMap = service.parseXml(inputStream);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 把version.xml放到网络上，然后获取文件信息
					// InputStream inStream =
					// UpdateParseXmlService.class.getClassLoader().getResourceAsStream("version.xml");
					// 解析XML文件。 由于XML文件比较小，因此使用DOM方式进行解析
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
		// 版本判断
		if (serviceCode > versionCode) {
			return true;
		}

		return false;

	}

	/**
	 * 获取软件版本号
	 * 
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = context.getPackageManager().getPackageInfo(
					"com.digdream.tasbook", 0).versionCode;
			System.out.println("versioncode"+versionCode);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 显示软件更新对话框
	 */
	private void showNoticeDialog() {
		// 构造对话框
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_update_title);
		builder.setMessage(R.string.soft_update_info);
		// 更新
		builder.setPositiveButton(R.string.soft_update_updatebtn,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 显示下载对话框
						showDownloadDialog();
					}
				});
		// 稍后更新
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
	 * 显示软件下载对话框
	 */
	public void showDownloadDialog() {
		// 构造软件下载对话框
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_updating);
		// 给下载对话框增加进度条
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		// 取消更新
		builder.setNegativeButton(R.string.soft_update_cancel,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 设置取消状态
						
					}
				});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		// 现在文件
		downloadApk();
	}

	/**
	 * 下载apk文件
	 */
	private void downloadApk() {
		// 启动新线程下载软件
		new downloadApkThread().start();
	}

	/**
	 * 下载文件线程
	 * 
	 * @author coolszy
	 * @date 2012-4-26
	 * @blog http://blog.92coding.com
	 */
	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory()
							+ "/";
					mSavePath = sdpath + "download";
					URL url = new URL(DownloadUrl);
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入流
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(mSavePath, Name);
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do {
						int numread = is.read(buf);
						count += numread;
						// 计算进度条位置
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// 点击取消就停止下载.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 取消下载对话框显示
			mDownloadDialog.dismiss();
		}
	};

	/**
	 * 安装APK文件
	 */
	private void installApk() {
		File apkfile = new File(mSavePath, Name);
		if (!apkfile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
}
