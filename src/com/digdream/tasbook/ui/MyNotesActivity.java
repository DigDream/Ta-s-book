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
		// ���DataBase�������
		setCursor = myToDoDB.getSettings();
		initSettings(this);
		emptyInfo();

		// ��myListView���OnItemClickListener
		myListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// ��myCursor�Ƶ���������ֵ
						myCursor.moveToPosition(arg2);
						// ȡ���ֶ�_id��ֵ
						_id = myCursor.getInt(0);
						title = myCursor.getString(1);
						content = myCursor.getString(2);
						use_pw = myCursor.getString(4);
						if (use_pw.equals("��")) {
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
						// ��myCursor�Ƶ���������ֵ
						myCursor.moveToPosition(arg2);
						// ȡ���ֶ�_id��ֵ
						_id = myCursor.getInt(0);
						use_pw = myCursor.getString(4);
						if (use_pw.equals("��")) {
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
		// ���MENU
		menu.add(Menu.NONE, MENU_ADD, 0, "����").setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(Menu.NONE, MENU_SET, 0, "����").setIcon(
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
		mTitle.setText("�ҵıʼ�");
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
				.setTitle("����������")
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
											"�������", Toast.LENGTH_LONG).show();
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
				.setTitle("ɾ��")
				.setMessage("ȷ��Ҫɾ����")
				.setPositiveButton(R.string.str_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								if (_id == 0)
									return;
								// ɾ������
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
		// newһ��Intent���󣬲�ָ��Ҫ������class
		Intent intent = null;
		intent = new Intent(this, mActivities[index]);

		// newһ��Bundle���󣬲���Ҫ���ݵ����ݴ���
		Bundle bundle = new Bundle();
		bundle.putInt("id", _id);
		bundle.putString("title", title);
		bundle.putString("content", content);
		bundle.putString("use_pw", use_pw);
		bundle.putString("op", op);
		bundle.putString("strPW", strPW);
		bundle.putString("strOrderItem", strOrderItem);
		bundle.putString("strOrderSort", strOrderSort);
		// ��Bundle����assign��Intent
		intent.putExtras(bundle);

		// ����һ���µ�Activity
		this.startActivity(intent);
		// �ر�ԭ����Activity
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
		// new SimpleCursorAdapter����myCursor����
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
