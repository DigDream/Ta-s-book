package com.digdream.tasbook.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.digdream.tasbook.AppInfo;
import com.digdream.tasbook.R;
import com.digdream.tasbook.adapter.ShareBookAdapter;
import com.digdream.tasbook.entity.BookMessage;
import com.digdream.tasbook.util.ACache;
import com.digdream.tasbook.util.URLProtocol;
import com.digdream.tasbook.view.SingleLayoutListView;
import com.digdream.tasbook.view.SingleLayoutListView.OnLoadMoreListener;
import com.digdream.tasbook.view.SingleLayoutListView.OnRefreshListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class FindBookFragment extends Fragment {
	private static final String TAG = "Ѱ�����";
	private View view;
	private SingleLayoutListView mListView;
	private List<BookMessage> data = new ArrayList<BookMessage>();
	private ACache mCache;
	private static final int LOAD_DATA_FINISH = 10;
	private static final int REFRESH_DATA_FINISH = 11;
	private int mCount = 10;
	private ShareBookAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_findbook, null);// ע�ⲻҪָ������ͼ

		findViewsById();
		return view;
	}

	private void findViewsById() {
		mListView = (SingleLayoutListView) view
				.findViewById(R.id.share_book_list_view);
		// �رռ��ظ���
		mListView.setCanLoadMore(false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// ��ʼ������
		mCache = ACache.get(this.getActivity());
		initData();
	}

	private Handler mHandler = new Handler() {

		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_DATA_FINISH:
				if (mAdapter != null) {
					mAdapter.data = (ArrayList<BookMessage>) msg.obj;
					mAdapter.notifyDataSetChanged();
					mListView.setCanLoadMore(false);
				}
				mListView.onRefreshComplete(); // ����ˢ�����
				break;
			case LOAD_DATA_FINISH:
				if (mAdapter != null) {
					mAdapter.data.addAll((ArrayList<BookMessage>) msg.obj);
					mAdapter.notifyDataSetChanged();
				}
				mListView.onLoadMoreComplete(); // ���ظ������
				break;
			}
		};
	};

	private void initData() {
		mAdapter = new ShareBookAdapter(this.getActivity(), data);
		mListView.setAdapter(mAdapter);
		mListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO ����ˢ��
				Log.e(TAG, "onRefresh");
				// loadData(0);
				ObtainSharedBookByAsyncHttpClientPost("test", 1);
				Message _Msg = mHandler
						.obtainMessage(REFRESH_DATA_FINISH, data);
				mHandler.sendMessage(_Msg);
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
		String mTag = "test";
		JSONArray testJsonArray = mCache.getAsJSONArray("sharebookslist");
		if (testJsonArray == null) {

		} else {
			for (int i = 0; i < testJsonArray.length(); i++) {

				try {
					JSONObject addrJsonObj = testJsonArray.getJSONObject(i);
					String title = addrJsonObj.getString("title");

					String summary;
					summary = addrJsonObj.getString("summary");
					String tag = addrJsonObj.getString("tag");
					String uid = addrJsonObj.getString("uid");
					String iconurl = addrJsonObj.getString("iconurl");
					String sharetime = addrJsonObj.getString("sharetime");
					Log.e(tag, "ceshi" + title + "summary" + summary + "tag"
							+ tag + "uid" + uid + "iconurl" + iconurl
							+ "sharetime" + sharetime);
					BookMessage msg = null;
					msg = new BookMessage(title, summary, sharetime);
					msg.setIcon_id(iconurl);
					msg.setTag(tag);
					msg.setShareAuthor(uid);
					data.add(msg);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		// ������Ҫ��ȡ����json��Ϣ��
		ObtainSharedBookByAsyncHttpClientPost(mTag, 1);
	}

	/**
	 * ����������~
	 * 
	 * @param type
	 * @date 2013-12-13 ����10:14:08
	 * @author JohnWatson
	 * @version 1.0
	 */
	public void loadData(final int type) {
		new Thread() {
			@Override
			public void run() {
				List<BookMessage> datahandler = new ArrayList<BookMessage>();

				// List<AppInfo> _List = null;
				switch (type) {
				case 0:
					mCount = 10;

					break;

				case 1:
					int _Index = mCount + 10;
					// ����������صڶ�ҳ��
					// ObtainSharedBookByAsyncHttpClientPost("test",_Index);
					for (int i = mCount + 1; i <= _Index; i++) {
						BookMessage msg2 = null;
						msg2 = new BookMessage(
								"��Android���ʵ����",
								"��������Ŀʵ��Ϊ���ߣ���ǳ����ؽ�����Android�ͻ��˿�����PHP����˿�����˼·�ͼ��ɡ�",
								"123457454");
						msg2.setIcon_id("http://img5.douban.com/spic/s25804527.jpg");
						msg2.setTag("android,php");
						msg2.setShareAuthor("5");
						datahandler.add(msg2);
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
							data);
					mHandler.sendMessage(_Msg);
				} else if (type == 1) {
					Message _Msg = mHandler.obtainMessage(LOAD_DATA_FINISH,
							datahandler);
					mHandler.sendMessage(_Msg);
				}
			}
		}.start();
	}

	/**
	 * ����AsyncHttpClient��Post��ʽ����ʵ��
	 * 
	 * @param tag
	 * 
	 */
	public void ObtainSharedBookByAsyncHttpClientPost(String tag, int page) {
		AsyncHttpClient client = new AsyncHttpClient(); // �����첽����Ŀͻ��˶���
		// ������������ķ�װ�Ķ���
		RequestParams params = new RequestParams();
		params.put("tag", tag); // ��������Ĳ������Ͳ���ֵ
		// params.put("page", page);
		CookieStore cookieStore = new PersistentCookieStore(this.getActivity());
		client.setCookieStore(cookieStore);
		HttpContext httpContext = client.getHttpContext();
		CookieStore cookies = (CookieStore) httpContext
				.getAttribute(ClientContext.COOKIE_STORE);
		if (cookies != null) {
			for (Cookie c : cookies.getCookies()) {
				Log.d("login activtity onsuccess before ~~" + c.getName(),
						c.getValue());
			}
		} else {
			Log.d("login activtity onsuccess  before~~", "cookies is null");
		}
		// ִ��post����
		client.post(URLProtocol.SHAREDALLBOOK_URL, params,
				new AsyncHttpResponseHandler() {
					private String json;

					/**
					 * �ɹ�����ķ��� statusCode:��Ӧ��״̬��; headers:��Ӧ��ͷ��Ϣ ���� ��Ӧ��ʱ�䣬��Ӧ�ķ�����
					 * ; responseBody:��Ӧ���ݵ��ֽ�
					 */
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						if (statusCode == 200) {
							json = new String(responseBody);
							Log.i("tasbook", json);
							json.replaceAll("(\r\n|\r|\n|\n\r)", " ");
							try {
								JSONObject jsonobject = new JSONObject(json);
								int code = jsonobject.getInt("code");
								switch (code) {
								case URLProtocol.ERROR_SHAREBOOK_SUCCESS:
									// address��ֵ��һ��json������������Ҫ�Ȼ�ȡjson����
									JSONArray addrArrays = jsonobject
											.getJSONArray("result");
									mCache.put("sharebookslist", addrArrays,
											30 * 60);
									data.clear();
									// addrArrays��������������JSONObject������������ѭ��ȡ��
									// ���ȴӻ����ж�ȡ��Ϣ�����жϻ������Ƿ�
									for (int i = 0; i < addrArrays.length(); i++) {
										JSONObject addrJsonObj = addrArrays
												.getJSONObject(i);
										String title = addrJsonObj
												.getString("title");

										String summary = addrJsonObj
												.getString("summary");
										String tag = addrJsonObj
												.getString("tag");
										String uid = addrJsonObj
												.getString("uid");
										String iconurl = addrJsonObj
												.getString("iconurl");
										String sharetime = addrJsonObj
												.getString("sharetime");
										Log.e(tag, "ceshi" + title + "summary"
												+ summary + "tag" + tag + "uid"
												+ uid + "iconurl" + iconurl
												+ "sharetime" + sharetime);
										BookMessage msg = null;
										msg = new BookMessage(title, summary,
												sharetime);
										msg.setIcon_id(iconurl);
										msg.setTag(tag);
										msg.setShareAuthor(uid);
										data.add(msg);
									}

									break;
								case URLProtocol.ERROR_SHAREBOOK_FALSE:
									break;
								default:

								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

							// Intent intent = new Intent();
							// intent.setClass(LoginActivity.this,
							// MainTabActivity.class);
							// startActivity(intent);
							// finish();
						} else {

							System.out.println("test");
						}
					}

					/**
					 * ʧ�ܴ���ķ��� error����Ӧʧ�ܵĴ�����Ϣ��װ������쳣������
					 */
					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						error.printStackTrace();// �Ѵ�����Ϣ��ӡ���켣��
					}
				});
	}
}