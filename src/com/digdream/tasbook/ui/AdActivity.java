package com.digdream.tasbook.ui;

import cn.waps.AppConnect;
import cn.waps.AppListener;
import android.app.Activity;
import android.os.Bundle;

public class AdActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppConnect.getInstance(this).setOffersCloseListener(new AppListener(){ 
			 
			 @Override 
			 public void onOffersClose() { 
			  // TODO �رջ���ǽʱ�m�������� 
				 System.out.println("�رջ���ǽ");
				 finish();
			 } 
			}); 
		AppConnect.getInstance(this).showOffers(this); 
	}
	
}
