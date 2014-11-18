package com.digdream.tasbook.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.BDNotifyListener;//�����õ�λ�����ѹ��ܣ���Ҫimport����
//���ʹ�õ���Χ�����ܣ���Ҫimport������
import com.baidu.location.BDGeofence;
import com.baidu.location.BDLocationStatusCodes;
import com.baidu.location.GeofenceClient;
import com.baidu.location.GeofenceClient.OnAddBDGeofencesResultListener;
import com.baidu.location.GeofenceClient.OnGeofenceTriggerListener;
import com.baidu.location.GeofenceClient.OnRemoveBDGeofencesResultListener;
import com.baidu.location.LocationClientOption.LocationMode;
import com.digdream.tasbook.R;

public class LocationActivity extends Activity {

		private LocationClient mLocationClient;
		private TextView LocationResult;
		private Button startLocation;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_location);
			mLocationClient = new LocationClient(this.getApplicationContext());
			mLocationClient.registerLocationListener(new MyLocationListener());
			
			LocationResult = (TextView)findViewById(R.id.textView1);
			startLocation = (Button)findViewById(R.id.addfence);
			startLocation.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					InitLocation();
					
					if(startLocation.getText().equals(getString(R.string.startlocation))){
						mLocationClient.start();
						startLocation.setText(getString(R.string.stoplocation));
					}else{
						mLocationClient.stop();
						startLocation.setText(getString(R.string.startlocation));
					}
					
					
				}
			});
			
		}
		
		@Override
		protected void onStop() {
			// TODO Auto-generated method stub
			mLocationClient.stop();
			super.onStop();
		}

		private void InitLocation(){
			LocationClientOption option = new LocationClientOption();
			option.setLocationMode(LocationMode.Hight_Accuracy);//���ö�λģʽ
			//option.setCoorType(tempcoor);//���صĶ�λ����ǰٶȾ�γ�ȣ�Ĭ��ֵgcj02
			int span=1000;
			option.setScanSpan(span);//���÷���λ����ļ��ʱ��Ϊ5000ms
			option.setIsNeedAddress(true);
			mLocationClient.setLocOption(option);
		}
		
		/**
		 * ʵ��ʵλ�ص�����
		 */
		public class MyLocationListener implements BDLocationListener {

			@Override
			public void onReceiveLocation(BDLocation location) {
				//Receive Location 
				StringBuffer sb = new StringBuffer(256);
				sb.append("time : ");
				sb.append(location.getTime());
				sb.append("error code : ");
				sb.append(location.getLocType());
				sb.append("latitude : ");
				sb.append(location.getLatitude());
				sb.append("lontitude : ");
				sb.append(location.getLongitude());
				sb.append("radius : ");
				sb.append(location.getRadius());
				if (location.getLocType() == BDLocation.TypeGpsLocation){
					sb.append("speed : ");
					sb.append(location.getSpeed());
					sb.append("satellite : ");
					sb.append(location.getSatelliteNumber());
					sb.append("direction : ");
					sb.append("addr : ");
					sb.append(location.getAddrStr());
					sb.append(location.getDirection());
				} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
					sb.append("addr : ");
					sb.append(location.getAddrStr());
					//��Ӫ����Ϣ
					sb.append("operationers : ");
					sb.append(location.getOperators());
				}
				LocationResult.setText(sb.toString());
				Log.i("test", sb.toString());
			}
		}
	}