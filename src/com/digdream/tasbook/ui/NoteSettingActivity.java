package com.digdream.tasbook.ui;

import com.digdream.tasbook.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class NoteSettingActivity extends Activity {
	private EditText editText1;
	private EditText editText2;
	private EditText editText3;
	private RadioGroup radioGroup1;
	private RadioGroup radioGroup2;
	private RadioButton radioButton1;
	private RadioButton radioButton2;
	private RadioButton radioButton3;
	private RadioButton radioButton4;
	private Button button1;
	private String strPW;
	private String strOrderItem;
	private String strOrderSort;
	private TextView mTitle;
	private ImageView mBtnBack;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notesetting);
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("笔记的设置");
		mBtnBack = (ImageView) findViewById(R.id.btn_action_bar_left);
		mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}

		});
		editText1 = (EditText) findViewById(R.id.editText1);
		editText2 = (EditText) findViewById(R.id.editText2);
		editText3 = (EditText) findViewById(R.id.editText3);
		radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
		radioGroup2 = (RadioGroup) findViewById(R.id.radioGroup2);
		radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
		radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
		radioButton3 = (RadioButton) findViewById(R.id.radioButton3);
		radioButton4 = (RadioButton) findViewById(R.id.radioButton4);
		
		// 取得Intent中的Bundle对象
		Bundle bundle = this.getIntent().getExtras();
		strPW = bundle.getString("strPW");
		strOrderItem = bundle.getString("strOrderItem");
		strOrderSort = bundle.getString("strOrderSort");
		
		if(strOrderItem.equals("upt_date")){
			radioButton2.setChecked(true);
		}else{
			radioButton1.setChecked(true);
		}
		if(strOrderSort.equals("desc")){
			radioButton4.setChecked(true);
		}else{
			radioButton3.setChecked(true);
		}
		
		button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(
	        new View.OnClickListener() {
				public void onClick(View arg0) {
					submit();
				}
			}
	    );
		
		radioGroup1.setOnCheckedChangeListener(
			new RadioGroup.OnCheckedChangeListener() {
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					if(checkedId==radioButton2.getId()){
						strOrderItem = "upt_date";
						radioButton2.setChecked(true);
					}else{
						strOrderItem = "title";
						radioButton1.setChecked(true);
					}
					MyNotesActivity.myToDoDB.setSettings(2, strOrderItem);
				}
			}
		);
		
		radioGroup2.setOnCheckedChangeListener(
			new RadioGroup.OnCheckedChangeListener() {
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					if(checkedId==radioButton4.getId()){
						strOrderSort = "desc";
						radioButton4.setChecked(true);
					}else{
						strOrderSort = "asc";
						radioButton3.setChecked(true);
					}
					MyNotesActivity.myToDoDB.setSettings(3, strOrderSort);
				}
			}
		);
	}
	
	private void submit(){
		String oldPW = editText1.getText().toString();
		String newPW = editText2.getText().toString();
		String verPW = editText3.getText().toString();
		
		if(oldPW.equals(strPW)){
			if(oldPW.equals(newPW)){
				strDialog("错误", "新密码与原密码不能相同，请重新输入！");
			}else{
				if(newPW.equals(verPW)){
					MyNotesActivity.myToDoDB.setSettings(1, newPW);
					strDialog("正确", "密码已修改！");
					editText1.setText("");
					editText2.setText("");
					editText3.setText("");
					strPW = newPW;
				}else{
					strDialog("错误", "新密码两次输入不一致，请重新输入！");
				}
			}
		}else{
			strDialog("错误", "原密码输入错误，请重新输入！");
		}
	}
	
	private void strDialog(String title, String msg){
    	new AlertDialog.Builder(this)
    	.setTitle(title)
    	.setMessage(msg)
    	.setPositiveButton(R.string.str_ok,
    		new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialoginterface, int i) {
				}
			}
    	)
		.show();
    }
	
	protected void onDestroy(){
		MyNotesActivity.setCursor.requery();
		MyNotesActivity.initSettings(this);
		super.onDestroy();
    }
}