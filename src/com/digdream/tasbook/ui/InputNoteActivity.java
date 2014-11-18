package com.digdream.tasbook.ui;

import com.digdream.tasbook.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class InputNoteActivity extends Activity {
	private EditText editText1;
	private EditText editText2;
	private CheckBox checkBox1;
	private Button button1;
	private int id;
	private String title;
	private String content;
	private String use_pw;
	private String op;
	private TextView mTitle;
	private ImageView mBtnBack;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inputnote);
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("添加笔记");
		mBtnBack = (ImageView) findViewById(R.id.btn_action_bar_left);
		mBtnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});
		editText1 = (EditText) findViewById(R.id.editText1);
		editText2 = (EditText) findViewById(R.id.editText2);
		checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
		
		// 取得Intent中的Bundle对象
		Bundle bundle = this.getIntent().getExtras();
		id = bundle.getInt("id");
		title = bundle.getString("title");
		content = bundle.getString("content");
		use_pw = bundle.getString("use_pw");
		op = bundle.getString("op");
		if(op.equals("edit")){
			editText1.setText(title);
			editText2.setText(content);
			if(use_pw.equals("★")){
				checkBox1.setChecked(true);
			}
		}
		
		button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(
        	new View.OnClickListener() {
				public void onClick(View arg0) {
					saveDialog();
				}
			}
        );
	}
	
	private void saveDialog(){
    	new AlertDialog.Builder(this)
    	.setTitle("保存")
    	.setMessage("确认要保存吗？")
    	.setPositiveButton(R.string.str_ok,
    		new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialoginterface, int i) {
					saveTodo();
				}
			}
    	)
    	.setNegativeButton(R.string.str_cancel,
        	new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialoginterface, int i) {
    			}
    		}
        )
		.show();
    }
	
	private void saveTodo(){
		if(editText1.getText().toString().equals("")){
			editText1.setText("无标题");
		}
		title = editText1.getText().toString();
		if(editText2.getText().toString().equals("")){
			editText2.setText("无内容");
		}
		content = editText2.getText().toString();
		use_pw = "";
		if(checkBox1.isChecked()){
			use_pw = "★";
		}
		if(op.equals("edit")){
			MyNotesActivity.myToDoDB.update(id, title, content, use_pw);
		}else{
			MyNotesActivity.myToDoDB.insert(title, content, use_pw);
		}
		MyNotesActivity.myCursor.requery();
		MyNotesActivity.myListView.invalidateViews();
		MyNotesActivity.emptyInfo();
		Toast.makeText(InputNoteActivity.this, "保存成功！", Toast.LENGTH_LONG).show();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode==KeyEvent.KEYCODE_BACK){
			String strPW = "";
			if(checkBox1.isChecked()){
				strPW = "★";
			}
			if(title.equals(editText1.getText().toString()) && content.equals(editText2.getText().toString()) && use_pw.equals(strPW)){
				InputNoteActivity.this.finish();
			}else{
				new AlertDialog.Builder(this)
		    	.setTitle("保存")
		    	.setMessage("内容已修改，要保存吗？")
		    	.setPositiveButton("保存",
		    		new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i) {
							saveTodo();
							InputNoteActivity.this.finish();
						}
					}
		    	)
		    	.setNegativeButton("放弃",
		        	new DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialoginterface, int i) {
		    				InputNoteActivity.this.finish();
		    			}
		    		}
		        )
				.show();
			}
			return true;
		}else{
			return super.onKeyDown(keyCode, event);
		}
	}
}