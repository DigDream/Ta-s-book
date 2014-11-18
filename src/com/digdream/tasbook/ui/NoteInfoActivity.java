package com.digdream.tasbook.ui;

import com.digdream.tasbook.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class NoteInfoActivity extends BaseActivity {

	private TextView mtitle;
	private ImageView btn_back;
	private TextView tv_noteinfo_notetitle;
	private TextView tv_noteinfo_noteauthor;
	private TextView tv_noteinfo_notetime;
	private TextView tv_noteinfo_notecommentcount;
	private TextView tv_noteinfo_content;
	@Override
	public void toNextActivity(int label) {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_noteinfo);
		initView();
		initData();
	}
	private void initView() {
		findViewsById();
	}

	private void findViewsById() {
		mtitle = (TextView) findViewById(R.id.title);
		mtitle.setText("± º«œÍ«È");
		btn_back = (ImageView) findViewById(R.id.btn_action_bar_left);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		tv_noteinfo_notetitle = (TextView) findViewById(R.id.tv_noteinfo_notetitle);
		tv_noteinfo_noteauthor = (TextView) findViewById(R.id.tv_noteinfo_noteauthor);
		tv_noteinfo_notetime = (TextView) findViewById(R.id.tv_noteinfo_notetime);
		tv_noteinfo_notecommentcount = (TextView) findViewById(R.id.tv_noteinfo_notecommentcount);
		tv_noteinfo_content = (TextView) findViewById(R.id.tv_noteinfo_content);
	}
	private void initData() {
		 Bundle bundle = new Bundle();
         bundle = this.getIntent().getExtras();
         tv_noteinfo_notetitle.setText(bundle.getString("tv_noteinfo_notetitle"));
         tv_noteinfo_noteauthor.setText(bundle.getString("tv_noteinfo_noteauthor"));
         tv_noteinfo_notetime.setText(bundle.getString("tv_noteinfo_notetime"));
         tv_noteinfo_notecommentcount.setText(bundle.getString("tv_noteinfo_notecommentcount"));
         tv_noteinfo_content.setText(bundle.getString("tv_noteinfo_content"));
	}

}
