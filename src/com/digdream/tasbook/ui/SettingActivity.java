package com.digdream.tasbook.ui;

import com.digdream.tasbook.R;
import com.digdream.tasbook.UpdateManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity {

	private TextView mtitle;
	private ImageView btn_back;
	private TableRow btn_update;
	private View btn_about;
	private TableRow btn_feedback;
	private TableRow btn_usersetting;
	private TableRow btn_setting;
	private UpdateManager manager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		findViewsById();
	}

	private void findViewsById() {
		mtitle = (TextView) findViewById(R.id.title);
		mtitle.setText("设置");
		btn_back = (ImageView) findViewById(R.id.btn_action_bar_left);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btn_update = (TableRow) findViewById(R.id.more_page_row6);
		btn_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				manager = new UpdateManager(SettingActivity.this);
				// 检查软件更新
				manager.check();
				if (manager.update == false) {
					Toast.makeText(SettingActivity.this, "不需要更新",
							Toast.LENGTH_LONG).show();
				} else {
					new AlertDialog.Builder(
							com.digdream.tasbook.ui.SettingActivity.this)
							.setCancelable(false)
							.setTitle("温馨提示")
							.setMessage(R.string.soft_update_info)
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											manager.showDownloadDialog();
										}
									})
							.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {

										}
									}).show();
				}
			}

		});
		btn_about = (TableRow) findViewById(R.id.more_page_row7);
		btn_about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						com.digdream.tasbook.ui.SettingActivity.this,
						AboutActivity.class);
				startActivity(intent);

			}

		});
		btn_feedback = (TableRow) findViewById(R.id.more_page_row5);
		btn_feedback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(SettingActivity.this, "感谢您的关注",
						Toast.LENGTH_LONG).show();

			}

		});
		btn_usersetting = (TableRow) findViewById(R.id.more_page_row1);
		btn_usersetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(SettingActivity.this, "感谢您的关注",
						Toast.LENGTH_LONG).show();

			}

		});
		btn_setting = (TableRow) findViewById(R.id.more_page_row4);
		btn_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Toast.makeText(SettingActivity.this, "感谢您的关注",
						Toast.LENGTH_LONG).show();
			}

		});
	}
}
