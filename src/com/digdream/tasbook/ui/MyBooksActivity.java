package com.digdream.tasbook.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.digdream.tasbook.R;
import com.digdream.tasbook.adapter.SwipeAdapter;
import com.digdream.tasbook.bean.Book;
import com.digdream.tasbook.database.SQLiteHelper;
import com.digdream.tasbook.database.TableColumns.FilesColumns;
import com.digdream.tasbook.entity.WXMessage;
import com.digdream.tasbook.util.FileUtil;
import com.digdream.tasbook.view.SwipeListView;

public class MyBooksActivity extends Activity {
	private List<WXMessage> data = new ArrayList<WXMessage>();
	private SwipeListView mListView;
	private TextView mTitle;
	private ImageView mBtnBack;
	private TextView mAdd;
	private Button mScanAdd;
	private View Dialogview;
	private Button mHandAdd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mybooks);
		findViewsById();
		initData();
		FileUtil.flag = 0;
	}

	private void findViewsById() {
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("�ҵ����");
		mAdd = (TextView) findViewById(R.id.textView_add);
		mBtnBack = (ImageView) findViewById(R.id.btn_action_bar_left);
		mBtnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});
		ImageView btnPopup = (ImageView) findViewById(R.id.book_center_flag);
		btnPopup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog();
			}

		});

		mListView = (SwipeListView) findViewById(R.id.mListView);

	}

	private void showDialog() {
		Dialogview = getLayoutInflater().inflate(R.layout.photo_choose_dialog,
				null);
		Dialog dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
		dialog.setContentView(Dialogview, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		// ������ʾ����
		window.setWindowAnimations(R.style.main_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = 0;
		wl.y = getWindowManager().getDefaultDisplay().getHeight();
		// ������������Ϊ�˱�֤��ť����ˮƽ����
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

		// ������ʾλ��
		dialog.onWindowAttributesChanged(wl);
		// ���õ����Χ��ɢ
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		findDialogViewsById();
	}

	private void findDialogViewsById() {
		mScanAdd = (Button) Dialogview.findViewById(R.id.scan_add);
		mScanAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						com.digdream.tasbook.ui.MyBooksActivity.this,
						CaptureActivity.class);
				com.digdream.tasbook.ui.MyBooksActivity.this
						.startActivity(intent);

			}

		});
		mHandAdd = (Button) Dialogview.findViewById(R.id.hand_add);
		mHandAdd.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(com.digdream.tasbook.ui.MyBooksActivity.this,HandAddBookActivity.class);
				com.digdream.tasbook.ui.MyBooksActivity.this.startActivity(intent);
			}
			
		});
	}

	private void initData() {
		// ��������
		if (new SQLiteHelper(this).isEmpty()) {
			mListView.setVisibility(View.GONE);
			mAdd.setVisibility(View.VISIBLE);
		} else {
			mListView.setVisibility(View.GONE);
			mAdd.setVisibility(View.VISIBLE);
			Log.i("123", "12345");
			new DataTask().execute();
			SwipeAdapter mAdapter = new SwipeAdapter(this, data);
			mAdapter.setOnRightItemClickListener(new SwipeAdapter.onRightItemClickListener() {

				@Override
				public void onRightItemClick(View v, final int position) {

					new AlertDialog.Builder(
							com.digdream.tasbook.ui.MyBooksActivity.this)
							.setCancelable(false)
							.setTitle("��ܰ��ʾ")
							.setMessage("��ȷ��Ҫɾ����ɾ���󲻿ɻָ���")
							.setPositiveButton("ȷ��",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											SQLiteHelper sqlite = new SQLiteHelper(
													MyBooksActivity.this);
											SQLiteDatabase db = sqlite
													.getReadableDatabase();
											String[] whereArgs = { String
													.valueOf(position) };
											db.delete("mybook", "_id=?",
													whereArgs);
											db.close();
										}
									})
							.setNegativeButton("ȡ��",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											finish();
											com.digdream.tasbook.ui.MyBooksActivity.this
													.stopService(getIntent());
											System.exit(0);
										}
									}).show();

				}
			});

			mListView.setAdapter(mAdapter);
		}

		/*
		 * for (int i = 0; i < 50; i++) { WXMessage msg = null; if (i % 3 == 0)
		 * { msg = new WXMessage("��Ѷ����", "�����ձ����ģ�ϰ��ƽ������ë����6����Ҫ�۵�", "����8:44");
		 * msg.setIcon_id(R.drawable.qq_icon); } else if (i % 3 == 1) { msg =
		 * new WXMessage("���ĺ�", "CSDN��2013�������߼���Ӱ������˾", "����8:49");
		 * msg.setIcon_id(R.drawable.wechat_icon); } else { msg = new
		 * WXMessage("΢���Ķ�", "��Ů�ݸ������Ӹ������Ѵ�绰", "��������");
		 * msg.setIcon_id(R.drawable.qq_icon); }
		 * 
		 * data.add(msg); }
		 */

	}

	private class DataTask extends AsyncTask<Void, Void, ArrayList<Book>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mListView.setVisibility(View.GONE);
		}

		@Override
		protected ArrayList<Book> doInBackground(Void... params) {
			ArrayList<Book> result = new ArrayList<Book>();
			SQLiteHelper sqlite = new SQLiteHelper(MyBooksActivity.this);
			SQLiteDatabase db = sqlite.getReadableDatabase();
			Cursor c = null;
			try {
				c = db.rawQuery("SELECT " + FilesColumns.COL_ID + ","
						+ FilesColumns.COL_ISBN10 + ","
						+ FilesColumns.COL_ISBN13 + "," + FilesColumns.COL_NAME
						+ "," + FilesColumns.COL_AUTHOR + ","
						+ FilesColumns.COL_SUMMARY + ","
						+ FilesColumns.COL_PUBLISHER + ","
						+ FilesColumns.COL_IMAGE + ","
						+ FilesColumns.COL_PUBLISHER + " FROM mybook", null);
				while (c.moveToNext()) {
					Book po = new Book();
					int index = 0;
					po._id = c.getString(index++);
					po.isbn10 = c.getString(index++);
					po.isbn13 = c.getString(index++);
					po.name = c.getString(index++);
					po.author = c.getString(index++);
					po.summary = c.getString(index++);
					po.publisher = c.getString(index++);
					po.image = c.getString(index++);
					po.classify = c.getString(index++);
					result.add(po);
				}
			} finally {
				if (c != null)
					c.close();
			}
			db.close();

			return result;

		}

		@SuppressWarnings("null")
		@Override
		protected void onPostExecute(ArrayList<Book> result) {
			super.onPostExecute(result);
			WXMessage msg = null;
			if (result != null) {
				if (result.size() != 0) {
					for (int i = 0; i < result.size(); i++) {
						Book o = result.get(i);
						System.out.println(o.getName() + o.getSummary()
								+ o.getAuthor());
						msg = new WXMessage(o.getName(), o.getSummary(),
								o.getAuthor());
						msg.setIcon_id(o.getImage());
						data.add(msg);
					}
					mAdd.setText("��ûͼ���ء�");
				}

				mListView.setVisibility(View.VISIBLE);
				mAdd.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// ��ActionBarͼ�걻���ʱ����
			Intent intent = new Intent();
			intent.setClass(MyBooksActivity.this, AddBookActivity.class);
			startActivity(intent);
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
