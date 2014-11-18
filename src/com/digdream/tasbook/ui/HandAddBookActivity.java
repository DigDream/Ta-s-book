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
import com.digdream.tasbook.ui.AddBookActivity.MyThread;
import com.digdream.tasbook.util.IsbnXmlSax;
import com.digdream.tasbook.util.URLProtocol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HandAddBookActivity extends Activity {

	public static String RESULT_MESSAGE = null;
	public static int flag = 0;
	private EditText editIsbn;
	private ImageView mBtnBack;
	private TextView submitButton;
	private Book book;
	private EditText editTitle;
	private TextView mBookTitle;
	private TextView mBookSummary;
	private EditText editSummary;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_handaddbook);
		findViewsById();
	}

	private void findViewsById() {
		editIsbn = (EditText) findViewById(R.id.e1);
		editTitle = (EditText) findViewById(R.id.editTitle);
		mBookTitle = (TextView) findViewById(R.id.mBookTitle);
		mBookSummary = (TextView) findViewById(R.id.mBookSummary);
		editSummary = (EditText) findViewById(R.id.editSummary);
		mBtnBack = (ImageView) findViewById(R.id.btn_action_bar_left);
		mBtnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});
		submitButton = (TextView) findViewById(R.id.t4);
		submitButton.setOnClickListener(new OnClickListener() {

			private String mIsbn;

			@Override
			public void onClick(View v) {
				if (flag == 0) {
					// 这里先获取输入框的值，然后连接豆瓣api查询，返回结果.
					mIsbn = editIsbn.getText().toString();
					com.digdream.tasbook.ui.HandAddBookActivity.RESULT_MESSAGE = editIsbn
							.getText().toString();

					if (TextUtils.isEmpty(mIsbn)) {
						Toast.makeText(HandAddBookActivity.this, "存在网络",
								Toast.LENGTH_LONG).show();
						return;
					}

					//
					MyThread m = new MyThread();
					new Thread(m).start();
					submitButton.setText("确认添加");
					flag = 1;
				} else {
					new AddBookTask().execute();
					Toast.makeText(HandAddBookActivity.this, "已添加成功",
							Toast.LENGTH_LONG).show();
					Intent intent = new Intent(com.digdream.tasbook.ui.HandAddBookActivity.this, MyBooksActivity.class);
			        startActivity(intent);			
					flag = 0;
				}

				
			}

		});
	}

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

				updateBookInfoView(summary);
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

			handler.sendEmptyMessage(0);

		}

		private void updateBookName(final String name) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					editTitle.setVisibility(View.VISIBLE);
					editTitle.setText(name);
					mBookTitle.setVisibility(View.VISIBLE);
				}
			});
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
				editSummary.setVisibility(View.VISIBLE);
				editSummary.setText(string);
				mBookSummary.setVisibility(View.VISIBLE);
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

			SQLiteHelper sqlite = new SQLiteHelper(HandAddBookActivity.this);
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
