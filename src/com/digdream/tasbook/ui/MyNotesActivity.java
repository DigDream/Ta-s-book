package com.digdream.tasbook.ui;

import com.digdream.tasbook.R;
import com.digdream.tasbook.database.ToDoDB;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MyNotesActivity extends Activity {
	public static ToDoDB myToDoDB;
	public static Cursor myCursor;
	public static Cursor setCursor;
	public static ListView myListView;
	private static TextView textView1;
	private TextView buttonTextView;
	private EditText buttonEditText;
	private int _id;
	private String title;
	private String content;
	private String use_pw;
	private static String strPW;
	private static String strOrderItem;
	private static String strOrderSort;
	protected final static int MENU_ADD = Menu.FIRST;
	protected final static int MENU_SET = Menu.FIRST + 1;
	protected final static int MENU_ABOUT = Menu.FIRST + 2;
	protected final static int MENU_EXIT = Menu.FIRST + 3;
	Class<?> mActivities[] = { InputNoteActivity.class,
			NoteSettingActivity.class };

	private TextView mTitle;
	private ImageView mBtnBack;
	private Button but_add = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mynotes);
		findViewsById();
		initData();
	}

	private void initData() {
		
		buttonTextView = new TextView(this);

		myToDoDB = new ToDoDB(this);
		// 获得DataBase里的数据
		setCursor = myToDoDB.getSettings();
		initSettings(this);
		emptyInfo();

		// 将myListView添加OnItemClickListener
		myListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// 将myCursor移到所单击的值
						myCursor.moveToPosition(arg2);
						// 取得字段_id的值
						_id = myCursor.getInt(0);
						title = myCursor.getString(1);
						content = myCursor.getString(2);
						use_pw = myCursor.getString(4);
						if (use_pw.equals("★")) {
							buttonEditText = new EditText(MyNotesActivity.this
									);
							verifyDialog(1);
						} else {
							onListItemClick(0, "edit");
						}
					}
				});

		myListView
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						// 将myCursor移到所单击的值
						myCursor.moveToPosition(arg2);
						// 取得字段_id的值
						_id = myCursor.getInt(0);
						use_pw = myCursor.getString(4);
						if (use_pw.equals("★")) {
							buttonEditText = new EditText(MyNotesActivity.this
									);
							verifyDialog(2);
						} else {
							deleteDialog();
						}
						return false;
					}
				});

		myListView
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
					}

					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		// 添加MENU
		menu.add(Menu.NONE, MENU_ADD, 0, "新增").setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(Menu.NONE, MENU_SET, 0, "设置").setIcon(
				android.R.drawable.ic_menu_preferences);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case MENU_ADD:
			title = "";
			content = "";
			use_pw = "";
			onListItemClick(0, "add");
			break;
		case MENU_SET:
			onListItemClick(1, "set");
			break;
		}
		return true;
	}

	private void findViewsById() {
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("我的笔记");
		mBtnBack = (ImageView) findViewById(R.id.btn_action_bar_left);
		mBtnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});
		textView1 = (TextView) findViewById(R.id.textView1);
		myListView = (ListView) findViewById(R.id.myListView);
		ImageView btnPopup = (ImageView) findViewById(R.id.book_center_flag);
		btnPopup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				title = "";
				content = "";
				use_pw = "";
				onListItemClick(0, "add");
			}

		});
	}
	private void verifyDialog(final int op) {
		new AlertDialog.Builder(this)
				.setTitle("请输入密码")
				.setView(buttonEditText)
				.setPositiveButton(R.string.str_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								buttonTextView.setText(buttonEditText.getText()
										.toString());
								if (buttonTextView.getText().toString()
										.equals(strPW)) {
									switch (op) {
									case 1:
										onListItemClick(0, "edit");
										break;
									case 2:
										deleteDialog();
										break;
									}
								} else {
									Toast.makeText(
											MyNotesActivity.this,
											"密码错误！", Toast.LENGTH_LONG).show();
								}
							}
						})
				.setNegativeButton(R.string.str_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
							}
						}).show();
	}

	private void deleteDialog() {
		new AlertDialog.Builder(this)
				.setTitle("删除")
				.setMessage("确认要删除吗？")
				.setPositiveButton(R.string.str_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								if (_id == 0)
									return;
								// 删除数据
								myToDoDB.delete(_id);
								myCursor.requery();
								myListView.invalidateViews();
								_id = 0;
								emptyInfo();
							}
						})
				.setNegativeButton(R.string.str_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
							}
						}).show();
	}

	void onListItemClick(int index, String op) {
		// new一个Intent对象，并指定要启动的class
		Intent intent = null;
		intent = new Intent(this, mActivities[index]);

		// new一个Bundle对象，并将要传递的数据传入
		Bundle bundle = new Bundle();
		bundle.putInt("id", _id);
		bundle.putString("title", title);
		bundle.putString("content", content);
		bundle.putString("use_pw", use_pw);
		bundle.putString("op", op);
		bundle.putString("strPW", strPW);
		bundle.putString("strOrderItem", strOrderItem);
		bundle.putString("strOrderSort", strOrderSort);
		// 将Bundle对象assign给Intent
		intent.putExtras(bundle);

		// 调用一个新的Activity
		this.startActivity(intent);
		// 关闭原来的Activity
		// this.finish();
	}

	public static void initSettings(Context context) {
		setCursor.moveToFirst();
		strPW = setCursor.getString(1);
		setCursor.moveToNext();
		strOrderItem = setCursor.getString(1);
		setCursor.moveToNext();
		strOrderSort = setCursor.getString(1);

		myCursor = myToDoDB.select(strOrderItem, strOrderSort);
		// new SimpleCursorAdapter并将myCursor传入
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(context,
				R.layout.note_list, myCursor, new String[] { "use_pw", "title",
						"upt_date" }, new int[] { R.id.listTextView3,
						R.id.listTextView1, R.id.listTextView2 });
		myListView.setAdapter(adapter);
	}

	public static void emptyInfo() {
		if (myCursor.getCount() > 0) {
			textView1.setHeight(0);
		} else {
			textView1.setHeight(100);
		}
	}
	

}
