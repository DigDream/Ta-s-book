package com.digdream.tasbook.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
 

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.LatLngBounds.Builder;
import com.digdream.tasbook.R;
 
 
public class MapActivity extends Activity implements CloudListener{
	private static final String LTAG = MapActivity.class
			.getSimpleName();
    public MapView mapView = null;
    public BaiduMap baiduMap = null;
 
    // ��λ�������
    public LocationClient locationClient = null;
    //�Զ���ͼ��
    BitmapDescriptor mCurrentMarker = null;
    boolean isFirstLoc = true;// �Ƿ��״ζ�λ
 	private TextView mTitle;
	private ImageView mBtnBack;
 
    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view ���ٺ��ڴ����½��յ�λ��
            if (location == null || mapView == null)
                return;
             
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);    //���ö�λ����
             
             
            if (isFirstLoc) {
                isFirstLoc = false;
                 
                 
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);   //���õ�ͼ���ĵ��Լ����ż���
//              MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                baiduMap.animateMapStatus(u);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext
        // ע��÷���Ҫ��setContentView����֮ǰʵ��
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("�鿴���������");
		mBtnBack = (ImageView) findViewById(R.id.btn_action_bar_left);
		mBtnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});
         
        mapView = (MapView) this.findViewById(R.id.bmapView); // ��ȡ��ͼ�ؼ�����
        baiduMap = mapView.getMap();
        //������λͼ��
        baiduMap.setMyLocationEnabled(true);
         
        locationClient = new LocationClient(getApplicationContext()); // ʵ����LocationClient��
        locationClient.registerLocationListener(myListener); // ע���������
        this.setLocationOption();   //���ö�λ����
        locationClient.start(); // ��ʼ��λ
        // baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); // ����Ϊһ���ͼ
 
        // baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE); //����Ϊ���ǵ�ͼ
        // baiduMap.setTrafficEnabled(true); //������ͨͼ
 
    }
 
    // ����״̬ʵ�ֵ�ͼ�������ڹ���
    @Override
    protected void onDestroy() {
        //�˳�ʱ���ٶ�λ
        locationClient.stop();
        baiduMap.setMyLocationEnabled(false);
        // TODO Auto-generated method stub
        super.onDestroy();
        mapView.onDestroy();
        mapView = null;
    }
 
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mapView.onResume();
    }
 
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mapView.onPause();
    }
 
    /**
     * ���ö�λ����
     */
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // ��GPS
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// ���ö�λģʽ
        option.setCoorType("bd09ll"); // ���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
        option.setScanSpan(5000); // ���÷���λ����ļ��ʱ��Ϊ5000ms
        option.setIsNeedAddress(true); // ���صĶ�λ���������ַ��Ϣ
        option.setNeedDeviceDirect(true); // ���صĶ�λ��������ֻ���ͷ�ķ���
         
        locationClient.setLocOption(option);
    }

	@Override
	public void onGetDetailSearchResult(DetailSearchResult result, int error) {
		// TODO Auto-generated method stub
		if (result != null) {
			if (result.poiInfo != null) {
				Toast.makeText(MapActivity.this, result.poiInfo.title,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(MapActivity.this,
						"status:" + result.status, Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onGetSearchResult(CloudSearchResult result, int error) {
		// TODO Auto-generated method stub
		if (result != null && result.poiList != null
				&& result.poiList.size() > 0) {
			Log.d(LTAG, "onGetSearchResult, result length: " + result.poiList.size());
			baiduMap.clear();
			BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
			LatLng ll;
			LatLngBounds.Builder builder = new Builder();
			for (CloudPoiInfo info : result.poiList) {
				ll = new LatLng(info.latitude, info.longitude);
				OverlayOptions oo = new MarkerOptions().icon(bd).position(ll);
				baiduMap.addOverlay(oo);
				builder.include(ll);
			}
			LatLngBounds bounds = builder.build();
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
			baiduMap.animateMapStatus(u);
		}
	}
 
}