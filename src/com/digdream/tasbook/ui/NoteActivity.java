package com.digdream.tasbook.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.digdream.tasbook.R;
import com.digdream.tasbook.database.ToDoDB;

@SuppressLint("ValidFragment")
public class NoteActivity extends Fragment {
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
	private View view;
	private Button but_add = null;
	private Button but_setting;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_notet, null);// 注意不要指定父视图

		but_setting = (Button) view.findViewById(R.id.but_setting);
		but_add = (Button) view.findViewById(R.id.but_add);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		textView1 = (TextView) view.findViewById(R.id.textView1);
		myListView = (ListView) view.findViewById(R.id.myListView);
		buttonTextView = new TextView(this.getActivity());

		myToDoDB = new ToDoDB(this.getActivity());
		// 获得DataBase里的数据
		setCursor = myToDoDB.getSettings();
		initSettings(this.getActivity());
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
							buttonEditText = new EditText(NoteActivity.this
									.getActivity());
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
							buttonEditText = new EditText(NoteActivity.this
									.getActivity());
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

		but_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				title = "";
				content = "";
				use_pw = "";
				onListItemClick(0, "add");
			}
		});
		but_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onListItemClick(1, "set");
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

	private void verifyDialog(final int op) {
		new AlertDialog.Builder(this.getActivity())
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
											NoteActivity.this.getActivity(),
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
		new AlertDialog.Builder(this.getActivity())
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
		intent = new Intent(this.getActivity(), mActivities[index]);

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