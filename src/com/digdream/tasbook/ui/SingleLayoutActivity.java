package com.digdream.tasbook.ui;

import java.util.ArrayList;
import java.util.List;

import com.digdream.tasbook.AppInfo;
import com.digdream.tasbook.R;
import com.digdream.tasbook.view.SingleLayoutListView;
import com.digdream.tasbook.view.SingleLayoutListView.OnLoadMoreListener;
import com.digdream.tasbook.view.SingleLayoutListView.OnRefreshListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * ����ԣ������������ˢ�¡���ȫ�ֶ����ơ�
 * @date 2013-12-13 ����9:43:26
 * @author JohnWatson
 * @version 1.0
 */
@SuppressLint("HandlerLeak")
public class SingleLayoutActivity extends Activity implements OnClickListener {
	private static final String TAG = "SingleLayoutActivity";

	private static final int LOAD_DATA_FINISH = 10;
	private static final int REFRESH_DATA_FINISH = 11;

	private List<AppInfo> mList = new ArrayList<AppInfo>();
	private MyListAdapter mAdapter;
	private SingleLayoutListView mListView;
	private int mCount = 10;

	private Button mCanPullRefBtn, mCanLoadMoreBtn, mCanAutoLoadMoreBtn,
			mIsMoveToFirstItemBtn, mIsDoRefreshOnUIChanged;

	private Handler mHandler = new Handler() {

		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_DATA_FINISH:
				if (mAdapter != null) {
					mAdapter.mList = (ArrayList<AppInfo>) msg.obj;
					mAdapter.notifyDataSetChanged();
				}
				mListView.onRefreshComplete(); // ����ˢ�����
				break;
			case LOAD_DATA_FINISH:
				if (mAdapter != null) {
					mAdapter.mList.addAll((ArrayList<AppInfo>) msg.obj);
					mAdapter.notifyDataSetChanged();
				}
				mListView.onLoadMoreComplete(); // ���ظ������
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
		
		setTitle(getTitle());
		setContentView(R.layout.single_layout_activity);

		buildAppData();
		initView();
	}

	private void initView() {
		mAdapter = new MyListAdapter(this, mList);
		mListView = (SingleLayoutListView) findViewById(R.id.mListView);
		mListView.setAdapter(mAdapter);

		mListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO ����ˢ��
				Log.e(TAG, "onRefresh");
				loadData(0);
			}
		});

		mListView.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				// TODO ���ظ���
				Log.e(TAG, "onLoad");
				loadData(1);
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// �˴���������position��mAdapter.getItemId()��ȡ��һ��;
				Log.e(TAG, "click position:" + position);
				// Log.e(TAG,
				// "__ mAdapter.getItemId() = "+mAdapter.getItemId(position));
			}
		});

		mCanPullRefBtn = (Button) findViewById(R.id.canPullRefBtn);
		mCanLoadMoreBtn = (Button) findViewById(R.id.canLoadMoreFlagBtn);
		mCanAutoLoadMoreBtn = (Button) findViewById(R.id.autoLoadMoreFlagBtn);
		mIsMoveToFirstItemBtn = (Button) findViewById(R.id.isMoveToFirstItemBtn);
		mIsDoRefreshOnUIChanged = (Button) findViewById(R.id.mIsDoRefreshOnWindowFocused);
		
		mCanPullRefBtn.setOnClickListener(this);
		mCanLoadMoreBtn.setOnClickListener(this);
		mCanAutoLoadMoreBtn.setOnClickListener(this);
		mIsMoveToFirstItemBtn.setOnClickListener(this);
		mIsDoRefreshOnUIChanged.setOnClickListener(this);
		
		mListView.setCanLoadMore(true);
		mListView.setCanRefresh(true);
		mListView.setAutoLoadMore(true);
		mListView.setMoveToFirstItemAfterRefresh(true);
		mListView.setDoRefreshOnUIChanged(true);
		
		initBtnText();
	}
	
	/**
	 * ʵ����Btn������
	 * @date 2013-12-13 ����10:13:24
	 * @author JohnWatson
	 * @version 1.0
	 */
	private void initBtnText(){
		if(mListView.isCanRefresh()){
			mCanPullRefBtn.setText("��������ˢ��");
		}else{
			mCanPullRefBtn.setText("�ر�����ˢ��");
		}
		
		if(mListView.isCanLoadMore()){
			mCanLoadMoreBtn.setText("���ü��ظ���");
		}else{
			mCanLoadMoreBtn.setText("�رռ��ظ���");
		}
		
		if(mListView.isAutoLoadMore()){
			mCanAutoLoadMoreBtn.setText("�����Զ����ظ���");
		}else{
			mCanAutoLoadMoreBtn.setText("�ر��Զ����ظ���");
		}
		
		if (mListView.isMoveToFirstItemAfterRefresh()) {
			mIsMoveToFirstItemBtn.setText("�����ƶ�����һ��Item");
		} else {
			mIsMoveToFirstItemBtn.setText("�ر��ƶ�����һ��Item");
		}
		
		if (mListView.isDoRefreshOnUIChanged()) {
			mIsDoRefreshOnUIChanged.setText("������ʾ������Զ�����ˢ��");
		} else {
			mIsDoRefreshOnUIChanged.setText("�ر���ʾ������Զ�����ˢ��");
		}
	}
	
	@Override
	public void onClick(View pV) {
		switch (pV.getId()) {
		
		case R.id.canPullRefBtn:
			mListView.setCanRefresh(!mListView.isCanRefresh());
			if(mListView.isCanRefresh()){
				mCanPullRefBtn.setText("��������ˢ��");
			}else{
				mCanPullRefBtn.setText("�ر�����ˢ��");
			}
			break;
			
		case R.id.canLoadMoreFlagBtn:
			mListView.setCanLoadMore(!mListView.isCanLoadMore());
			if(mListView.isCanLoadMore()){
				mCanLoadMoreBtn.setText("���ü��ظ���");
			}else{
				mCanLoadMoreBtn.setText("�رռ��ظ���");
			}
			break;
		case R.id.autoLoadMoreFlagBtn:
			mListView.setAutoLoadMore(!mListView.isAutoLoadMore());
			if(mListView.isAutoLoadMore()){
				mCanAutoLoadMoreBtn.setText("�����Զ����ظ���");
			}else{
				mCanAutoLoadMoreBtn.setText("�ر��Զ����ظ���");
			}
			break;
			
		case R.id.isMoveToFirstItemBtn:
			mListView.setMoveToFirstItemAfterRefresh(!mListView
					.isMoveToFirstItemAfterRefresh());
			if (mListView.isMoveToFirstItemAfterRefresh()) {
				mIsMoveToFirstItemBtn.setText("�����ƶ�����һ��Item");
			} else {
				mIsMoveToFirstItemBtn.setText("�ر��ƶ�����һ��Item");
			}
			break;
			
		case R.id.mIsDoRefreshOnWindowFocused:
			mListView.setDoRefreshOnUIChanged(!mListView
					.isDoRefreshOnUIChanged());
			if (mListView.isDoRefreshOnUIChanged()) {
				mIsDoRefreshOnUIChanged.setText("������ʾ������Զ�����ˢ��");
			} else {
				mIsDoRefreshOnUIChanged.setText("�ر���ʾ������Զ�����ˢ��");
			}
			break;
		}
	}

	/**
	 * ����������~
	 * @param type 
	 * @date 2013-12-13 ����10:14:08
	 * @author JohnWatson
	 * @version 1.0
	 */
	public void loadData(final int type) {
		new Thread() {
			@Override
			public void run() {
				List<AppInfo> _List = null;
				switch (type) {
				case 0:
					mCount = 10;

					_List = new ArrayList<AppInfo>();
					for (int i = 1; i <= mCount; i++) {
						AppInfo ai = new AppInfo();

						ai.setAppIcon(BitmapFactory.decodeResource(
								getResources(), R.drawable.ic_launcher));
						ai.setAppName("Ӧ��Demo_" + i);
						ai.setAppVer("�汾: " + (i % 10 + 1) + "." + (i % 8 + 2)
								+ "." + (i % 6 + 3));
						ai.setAppSize("��С: " + i * 10 + "MB");

						_List.add(ai);
					}
					break;

				case 1:
					_List = new ArrayList<AppInfo>();
					int _Index = mCount + 10;

					for (int i = mCount + 1; i <= _Index; i++) {
						AppInfo ai = new AppInfo();

						ai.setAppIcon(BitmapFactory.decodeResource(
								getResources(), R.drawable.ic_launcher));
						ai.setAppName("Ӧ��Demo_" + i);
						ai.setAppVer("�汾: " + (i % 10 + 1) + "." + (i % 8 + 2)
								+ "." + (i % 6 + 3));
						ai.setAppSize("��С: " + i * 10 + "MB");

						_List.add(ai);
					}
					mCount = _Index;
					break;
				}

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (type == 0) { // ����ˢ��
				// Collections.reverse(mList); //����
					Message _Msg = mHandler.obtainMessage(REFRESH_DATA_FINISH,
							_List);
					mHandler.sendMessage(_Msg);
				} else if (type == 1) {
					Message _Msg = mHandler.obtainMessage(LOAD_DATA_FINISH,
							_List);
					mHandler.sendMessage(_Msg);
				}
			}
		}.start();
	}

	private void buildAppData() {
		for (int i = 1; i <= 10; i++) {
			AppInfo ai = new AppInfo();

			ai.setAppIcon(BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_launcher));
			ai.setAppName("Ӧ��Demo_" + i);
			ai.setAppVer("�汾: " + (i % 10 + 1) + "." + (i % 8 + 2) + "."
					+ (i % 6 + 3));
			ai.setAppSize("��С: " + i * 10 + "MB");

			mList.add(ai);
		}
	}

	private class MyListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		public List<AppInfo> mList;

		public MyListAdapter(Context pContext, List<AppInfo> pList) {
			mInflater = LayoutInflater.from(pContext);
			if (pList != null) {
				mList = pList;
			} else {
				mList = new ArrayList<AppInfo>();
			}
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
//			System.out.println("getItemId = " + position);
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (getCount() == 0) {
				return null;
			}
			// System.out.println("position = "+position);
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item, null);

				holder = new ViewHolder();
				holder.mImage = (ImageView) convertView
						.findViewById(R.id.ivIcon);
				holder.mName = (TextView) convertView.findViewById(R.id.tvName);
				holder.mVer = (TextView) convertView.findViewById(R.id.tvVer);
				holder.mSize = (TextView) convertView.findViewById(R.id.tvSize);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			AppInfo ai = mList.get(position);
			holder.mImage.setImageBitmap(ai.getAppIcon());
			holder.mName.setText(ai.getAppName());
			holder.mVer.setText(ai.getAppVer());
			holder.mSize.setText(ai.getAppSize());

			return convertView;
		}
	}

	private static class ViewHolder {
		private ImageView mImage;
		private TextView mName;
		private TextView mVer;
		private TextView mSize;
	}

}
