package com.giant.sorm3.utils;

import com.giant.sorm3.annotation.Delete;
import com.giant.sorm3.annotation.Insert;
import com.giant.sorm3.annotation.Select;
import com.giant.sorm3.annotation.Update;

import java.lang.reflect.Method;


public class SormUtils {
	
	public static boolean isSelectMethod(Method method) {
		return method.isAnnotationPresent(Select.class);
	}
	
	public static boolean isDMLMethod(Method method) {
		return isInsertMethod(method)||isDeleteMethod(method)||isUpdateMethod(method);
		
	}
	
	public static boolean isInsertMethod(Method method) {
		return method.isAnnotationPresent(Insert.class);
	}
	
	public static boolean isDeleteMethod(Method method) {
		return method.isAnnotationPresent(Delete.class);
	}
	
	public static boolean isUpdateMethod(Method method) {
		return method.isAnnotationPresent(Update.class);
	}
	
	public static boolean hasParam(String sql) {
		if(sql==null) {
			return false;
		}
		return sql.contains("#{")&&sql.contains("}")&&sql.indexOf("#{")<sql.indexOf("}");
	}
}
