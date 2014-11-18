package com.digdream.tasbook.util;

public class URLProtocol {
	//ע����Ǹ��ӿ�
	public static final int CMD_REGISTER = 1;
	//ע���ַ
	public static final String ROOT = "http://182.92.180.94/Tasbook/index.php/home/AndroidRegister/index";
	public static final String LOGIN_URL = "http://182.92.180.94/Tasbook/index.php/home/AndroidLogin/index";
	//����api��ַ
	public static final String DOUBAN_API = "http://api.douban.com/book/subject/isbn/";
	//apk�汾��Ϣ
	public static final String queryString = "http://182.92.180.94/Tasbook/version.xml";
	public static final String SHAREDBOOK_URL = "http://182.92.180.94/Tasbook/index.php/home/AndroidSharedBooks/index";
	public static final String SHAREDALLBOOK_URL = "http://182.92.180.94/Tasbook/index.php/home/AndroidSharedBooks/indexall";
	public static final String SHARENOTE_URL = "http://182.92.180.94/Tasbook/index.php/home/AndroidNotes/indexall";
	//ע��ɹ����ص�code
	public static final int SUCCESS = 10000;
	public static final int FALSE = 10001;
	public static final int ERROR_SIGNUP_NAME_REPEAT = 10001;
	public static final int ERROR_SIGNUP_PHONE_REPEAT = 10002;
	public static final int ERROR_SIGNUP_DATABASE_FALSE = 10003;
	public static final int ERROR_SIGNIN_SUCCESS = 20000;
	public static final int ERROR_SIGNIN_FALSE = 20001;
	public static final int ERROR_SIGNIN_LOCK = 20002;
	public static final int ERROR_SHAREBOOK_SUCCESS = 10000;
	public static final int ERROR_SHAREBOOK_FALSE = 10001;
}