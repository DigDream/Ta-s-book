package com.digdream.tasbook.ui;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import com.digdream.tasbook.R;
import com.digdream.tasbook.bean.Book;
import com.digdream.tasbook.database.SQLiteHelper;
import com.digdream.tasbook.database.TableColumns.FilesColumns;
import com.digdream.tasbook.ui.HandAddBookActivity.MyThread;
import com.digdream.tasbook.util.IsbnXmlSax;
import com.digdream.tasbook.util.URLProtocol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddBookActivity extends Activity {
	boolean isEdit1 = false;
	boolean isEdit2 = false;
	EditText editMoney = null;
	TextView leftText = null;
	TextView submitText = null;
	final int MAX_LENGTH = 100;
	int Rest_Length = MAX_LENGTH;
	private EditText editReason;
	private ImageView mBtnBack;

	public static String RESULT_MESSAGE = null;
	public static Bitmap RESULT_BITMAP = null;
	public static int flag = 0;
	private TextView mResultTextView;
	private ImageView mResultImageView;
	private Book book;
	private String mStringBookInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_addbook);
		initView();
		InitScanResult();
	}

	private void initView() {
		editMoney = (EditText) findViewById(R.id.e1);
		editReason = (EditText) findViewById(R.id.e2);
		editReason.addTextChangedListener(listener1);
		leftText = (TextView) findViewById(R.id.t3);
		editMoney.addTextChangedListener(listener2);
		mBtnBack = (ImageView) findViewById(R.id.btn_action_bar_left);
		mBtnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});
		submitText = (TextView) findViewById(R.id.t4);
		submitText.setVisibility(View.INVISIBLE);
		mResultTextView = (TextView) findViewById(R.id.tv_result);
		mResultImageView = (ImageView) findViewById(R.id.iv_result);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	TextWatcher listener1 = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			if (Rest_Length > 0) {
				Rest_Length = MAX_LENGTH - editReason.getText().length();
			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			leftText.setText("还可以输入" + Rest_Length + "字");
		}

		@Override
		public void afterTextChanged(Editable arg0) {
			leftText.setText("还可以输入" + Rest_Length + "字");
			if (editReason.length() > 0) {
				isEdit2 = true;
			} else {
				isEdit2 = false;
			}
			if (isEdit1 && isEdit2) {
				submitText.setVisibility(View.VISIBLE);
				Animation shakeAnim = AnimationUtils.loadAnimation(
						AddBookActivity.this, R.anim.shake_y);
				submitText.startAnimation(shakeAnim);
			} else {
				submitText.setVisibility(View.INVISIBLE);
			}
		}
	};

	TextWatcher listener2 = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}

		@Override
		public void afterTextChanged(Editable arg0) {
			if (editMoney.length() > 0) {
				isEdit1 = true;
			} else {
				isEdit1 = false;
			}
			if (isEdit1 && isEdit2) {
				submitText.setVisibility(View.VISIBLE);
				Animation shakeAnim = AnimationUtils.loadAnimation(
						AddBookActivity.this, R.anim.shake_y);
				submitText.startAnimation(shakeAnim);
				submitText.setOnClickListener(new OnClickListener() {

					private String mIsbn;

					@Override
					public void onClick(View v) {
						if (flag == 0) {
							new AddBookTask().execute();
							Toast.makeText(AddBookActivity.this, "已添加成功",
									Toast.LENGTH_LONG).show();
							Intent intent = new Intent(
									com.digdream.tasbook.ui.AddBookActivity.this,
									MyBooksActivity.class);
							startActivity(intent);
						}

					}

				});

			} else {
				submitText.setVisibility(View.INVISIBLE);
			}
		}
	};

	class MyThread implements Runnable {
		public void run() {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String isbn = RESULT_MESSAGE;
			Looper.prepare();
			String[] strarray = isbn.split(":", 2);// 使用limit，最多分割成2个字符串
			isbn = strarray[1];
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL(
						URLProtocol.DOUBAN_API + isbn).openConnection();
				conn.setConnectTimeout(3000);
				conn.setRequestMethod("GET");
				if (conn.getResponseCode() != 200) {
					updateBookInfoView("无法获取图书信息。错误编号："
							+ conn.getResponseCode());
					return;
				}
				book = IsbnXmlSax.sax((GZIPInputStream) conn.getContent());
				String summary = book.getSummary();
				summary = summary.substring(0,
						summary.length() < 60 ? summary.length() : 60).concat(
						"...");
				System.out.println(book.getName() + book.getAuthor()
						+ book.getPublisher() + book.getIsbn13() + summary);
				String string = book.getName() + book.getAuthor()
						+ book.getPublisher() + book.getIsbn13() + summary;
				/*
				 * String string = String.format(mStringBookInfo,book.getName(),
				 * book.getAuthor(), book.getPublisher(), book.getIsbn13(),
				 * summary);
				 */
				new AddBookTask().execute();
				Toast.makeText(AddBookActivity.this, "已添加成功",
						Toast.LENGTH_LONG).show();
				updateBookInfoView(string);
				updateBookName(book.getName());
			} catch (MalformedURLException e1) {

				e1.printStackTrace();
			} catch (IOException e1) {

				e1.printStackTrace();
			}

			// DefaultHttpClient client = new DefaultHttpClient();

			System.out.println(URLProtocol.DOUBAN_API + isbn);
			// HttpGet request = new HttpGet(DOUBAN_API + isbn);
			/*
			 * try { //HttpResponse response = client.execute(request);
			 * 
			 * 
			 * } catch (Exception e) { e.printStackTrace(); }
			 */
			// mResultImageView.setImageBitmap(RESULT_BITMAP);

			RESULT_MESSAGE = null;
			RESULT_BITMAP = null;
			handler.sendEmptyMessage(0);

		}

		private void updateBookName(final String name) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					editMoney.setText(name);
				}
			});
		}
	}

	private void InitScanResult() {
		if (RESULT_MESSAGE != null && RESULT_BITMAP != null) {
			// mResultTextView.setText(RESULT_MESSAGE);
			MyThread m = new MyThread();
			new Thread(m).start();
			/*
			 * new Thread() {
			 * 
			 * @Override public void run() { super.run(); DefaultHttpClient
			 * client = new DefaultHttpClient(); String isbn = RESULT_MESSAGE;
			 * HttpGet request = new HttpGet(DOUBAN_API + isbn); try {
			 * HttpResponse response = client.execute(request); book =
			 * IsbnXmlSax .sax(response.getEntity().getContent()); String
			 * summary = book.getSummary(); summary = summary.substring(0,
			 * summary.length() < 60 ? summary.length() : 60) .concat("...");
			 * String string = String.format(mStringBookInfo, book.getName(),
			 * book.getAuthor(), book.getPublisher(), book.getIsbn13(),
			 * summary); updateBookInfoView(string); } catch (Exception e) {
			 * e.printStackTrace(); }
			 * mResultImageView.setImageBitmap(RESULT_BITMAP);
			 * 
			 * RESULT_MESSAGE = null; RESULT_BITMAP = null;
			 * handler.sendEmptyMessage(0); } }.start();
			 */

		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				break;
			}
		};
	};

	/**
	 * 更新图书信息
	 * 
	 * @param string
	 */
	private void updateBookInfoView(final String string) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mResultTextView.setText(string);
			}
		});
	}

	/** 添加图书 */
	private class AddBookTask extends AsyncTask<Void, Book, ArrayList<Book>> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();

		}

		@Override
		protected ArrayList<Book> doInBackground(Void... params) {

			// ~~~ 入库

			SQLiteHelper sqlite = new SQLiteHelper(AddBookActivity.this);
			SQLiteDatabase db = sqlite.getWritableDatabase();
			try {
				db.beginTransaction();

				SQLiteStatement stat = db
						.compileStatement("INSERT INTO mybook("
								+ FilesColumns.COL_NAME + ","
								+ FilesColumns.COL_AUTHOR + ","
								+ FilesColumns.COL_SUMMARY + ","
								+ FilesColumns.COL_IMAGE + ") VALUES(?,?,?,?)");

				String name = book.getName();
				int index = 1;
				stat.bindString(index++, book.getName());// title
				stat.bindString(index++, book.getAuthor());// title_pinyin
				stat.bindString(index++, book.getSummary());// path
				stat.bindString(index++, book.getImage());// last_access_time
				stat.execute();

				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.endTransaction();
				db.close();
			}
			return null;

			// ~~~ 查询数据

		}

		@Override
		protected void onProgressUpdate(final Book... values) {
		}

		@Override
		protected void onPostExecute(ArrayList<Book> result) {
			super.onPostExecute(result);

		}
	}
}
