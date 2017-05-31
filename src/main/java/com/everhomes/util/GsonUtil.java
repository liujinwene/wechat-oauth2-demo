package com.everhomes.util;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtil {
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();  
	
	public static String toJson(Object classType) {
		return gson.toJson(classType);
	}
	
	public static String toJson(Object object, Type type) {
		return gson.toJson(object, type);
	}
	
	public static <T> T fromJson(String json, Class<T> classType) {
		return gson.fromJson(json, classType);
	}
	
	public static <T> T fromJson(String json, Type type) {
		return gson.fromJson(json, type);
	}
}
