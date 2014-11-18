package com.digdream.tasbook.ui;

import com.digdream.tasbook.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends BaseActivity {
	
	

	private TextView mtitle;
	private ImageView btn_back;

	/* (non-Javadoc)
	 * @see com.digdream.tasbook.ui.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		findViewsById();
	}

	private void findViewsById() {
		mtitle = (TextView)findViewById(R.id.title);
		mtitle.setText("关于我们");
		btn_back = (ImageView) findViewById(R.id.btn_action_bar_left);
		btn_back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
		}
		);
		
	}

	@Override
	public void toNextActivity(int label) {


	}

}
