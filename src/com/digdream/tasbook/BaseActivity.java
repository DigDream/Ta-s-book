package com.digdream.tasbook;

import android.app.Activity;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public abstract void toNextActivity(int label);
	
	public void exitActivity(){
		
	}

}