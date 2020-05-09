package com.giant.commons.opeator;

public class StringOperator {
	
	public static String firstToLowerCase(String str) {
		return str.toLowerCase().substring(0,1)+str.substring(1);
	}
	
	/**
	 * 
	 * @param str 字符串
	 * @return	首字母大写的字符串
	 */
	public static String firstToUpperCase(String str){
		return str.substring(0,1).toUpperCase()+str.substring(1);
	}
}
