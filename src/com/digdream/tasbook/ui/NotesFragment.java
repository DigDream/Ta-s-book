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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.digdream.tasbook.R;
import com.digdream.tasbook.adapter.NotesAdapter;
import com.digdream.tasbook.entity.BookMessage;
import com.digdream.tasbook.entity.NoteMessage;
import com.digdream.tasbook.util.ACache;
import com.digdream.tasbook.util.URLProtocol;
import com.digdream.tasbook.view.SingleLayoutListView;
import com.digdream.tasbook.view.SingleLayoutListView.OnLoadMoreListener;
import com.digdream.tasbook.view.SingleLayoutListView.OnRefreshListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

public class NotesFragment extends Fragment {
	private static final String TAG = "笔记界面";
	private View view;
	private SingleLayoutListView mListView;
	private List<NoteMessage> data = new ArrayList<NoteMessage>();
	private ACache mCache;
	private NotesAdapter mAdapter;
	private static final int LOAD_DATA_FINISH = 10;
	private static final int REFRESH_DATA_FINISH = 11;
	private int mCount = 10;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_note, null);// 注意不要指定父视图
		findViewsById();
		return view;
	}

	private void findViewsById() {
		mListView = (SingleLayoutListView) view
				.findViewById(R.id.frame_listview_notes);
		// 关闭加载更多
		mListView.setCanLoadMore(false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		// 初始化缓存
		mCache = ACache.get(this.getActivity());
		initData();
	}

	private Handler mHandler = new Handler() {

		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_DATA_FINISH:
				if (mAdapter != null) {
					mAdapter.data = (ArrayList<NoteMessage>) msg.obj;
					mAdapter.notifyDataSetChanged();
					mListView.setCanLoadMore(false);
				}
				mListView.onRefreshComplete(); // 下拉刷新完成
				break;
			case LOAD_DATA_FINISH:
				if (mAdapter != null) {
					mAdapter.data.addAll((ArrayList<NoteMessage>) msg.obj);
					mAdapter.notifyDataSetChanged();
				}
				mListView.onLoadMoreComplete(); // 加载更多完成
				break;
			}
		};
	};

	private void initData() {
		mAdapter = new NotesAdapter(this.getActivity(), data);
		mListView.setAdapter(mAdapter);
		mListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO 下拉刷新
				Log.e(TAG, "onRefresh");
				// loadData(0);
				ObtainNotesByAsyncHttpClientPost("test", 1);
				Message _Msg = mHandler
						.obtainMessage(REFRESH_DATA_FINISH, data);
				mHandler.sendMessage(_Msg);
			}
		});

		mListView.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				// TODO 加载更多
				Log.e(TAG, "onLoad");
				loadData(1);
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 此处传回来的position和mAdapter.getItemId()获取的一致;
				Log.e(TAG, "click position:" + position);
				// Log.e(TAG,
				// "__ mAdapter.getItemId() = "+mAdapter.getItemId(position));
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				NoteMessage msg = data.get(position);
				bundle.putString("tv_noteinfo_notetitle", msg.getTitle());
				bundle.putString("tv_noteinfo_notecommentcount", String.valueOf(msg.getCommentCount()));
				bundle.putString("tv_noteinfo_notetime", msg.getTime());
				bundle.putString("tv_noteinfo_content", msg.getMsg());
				bundle.putString("tv_noteinfo_noteauthor", msg.getShareAuthor());
				intent.setClass(com.digdream.tasbook.ui.NotesFragment.this
						.getActivity(), NoteInfoActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		String mTag = "test";
		JSONArray testJsonArray = mCache.getAsJSONArray("sharenoteslist");
		if (testJsonArray == null) {

		} else {
			for (int i = 0; i < testJsonArray.length(); i++) {

				try {
					JSONObject addrJsonObj = testJsonArray.getJSONObject(i);
					String title = addrJsonObj.getString("title");
					String summary = addrJsonObj.getString("content");
					String uid = addrJsonObj.getString("uid");
					String sharetime = addrJsonObj.getString("time");
					NoteMessage msg = null;
					int commentCount = 0;
					msg = new NoteMessage(title, summary, sharetime,
							commentCount);
					msg.setShareAuthor(uid);
					data.add(msg);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		// 这里需要获取网络json信息。
		ObtainNotesByAsyncHttpClientPost(mTag, 1);
	}

	/**
	 * 加载数据啦~
	 * 
	 * @param type
	 * @date 2013-12-13 上午10:14:08
	 * @author JohnWatson
	 * @version 1.0
	 */
	public void loadData(final int type) {
		new Thread() {
			@Override
			public void run() {
				List<NoteMessage> datahandler = new ArrayList<NoteMessage>();

				// List<AppInfo> _List = null;
				switch (type) {
				case 0:
					mCount = 10;

					break;

				case 1:
					int _Index = mCount + 10;
					// 访问网络加载第二页。
					// ObtainSharedBookByAsyncHttpClientPost("test",_Index);
					for (int i = mCount + 1; i <= _Index; i++) {
						NoteMessage msg2 = null;
						msg2 = new NoteMessage(
								"【Android最佳实践】",
								"本书以项目实例为主线，由浅入深地讲解了Android客户端开发和PHP服务端开发的思路和技巧。",
								"123457454",0);
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

				if (type == 0) { // 下拉刷新
					// Collections.reverse(mList); //逆序
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
	 * 采用AsyncHttpClient的Post方式进行实现
	 * 
	 * @param tag
	 * 
	 */
	public void ObtainNotesByAsyncHttpClientPost(String tag, int page) {
		AsyncHttpClient client = new AsyncHttpClient(); // 创建异步请求的客户端对象
		// 创建请求参数的封装的对象
		RequestParams params = new RequestParams();
		params.put("tag", tag); // 设置请求的参数名和参数值
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
		// 执行post方法
		client.post(URLProtocol.SHARENOTE_URL, params,
				new AsyncHttpResponseHandler() {
					private String json;

					/**
					 * 成功处理的方法 statusCode:响应的状态码; headers:相应的头信息 比如 响应的时间，响应的服务器
					 * ; responseBody:响应内容的字节
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
									// address的值是一个json数组所以我们要先获取json数组
									JSONArray addrArrays = jsonobject
											.getJSONArray("result");
									mCache.put("sharenoteslist", addrArrays,
											30 * 60);
									data.clear();
									// addrArrays数组中又有两个JSONObject数据所以我们循环取出
									// 首先从缓存中读取信息，先判断缓存中是否
									for (int i = 0; i < addrArrays.length(); i++) {
										JSONObject addrJsonObj = addrArrays
												.getJSONObject(i);
										String title = addrJsonObj
												.getString("title");

										String summary = addrJsonObj
												.getString("content");

										String uid = addrJsonObj
												.getString("uid");

										String sharetime = addrJsonObj
												.getString("time");

										NoteMessage msg = null;
										int commentCount = 0;
										msg = new NoteMessage(title, summary,
												sharetime, commentCount);
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

						} else {

							System.out.println("test");
						}
					}

					/**
					 * 失败处理的方法 error：响应失败的错误信息封装到这个异常对象中
					 */
					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						error.printStackTrace();// 把错误信息打印出轨迹来
					}
				});
	}
}