package com.digdream.tasbook.data;

public class URLParam {
	public StringBuffer _query = new StringBuffer();
	
	public URLParam(URLParam urlParam){
		if(null != urlParam){
			_query.append(urlParam._query.toString());
		}
	}
	

}