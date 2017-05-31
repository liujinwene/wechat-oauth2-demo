package com.everhomes.util;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

@SuppressWarnings({"deprecation","resource"})
public class HttpUtil {
	
	public static String get(String url) {
		HttpClient httpclient = new DefaultHttpClient();
		try {
			//request
			HttpGet httpget = new HttpGet(url);
			return httpclient.execute(httpget, new BasicResponseHandler()); 
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}
}
