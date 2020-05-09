package com.giant.study.test;

import java.util.regex.Pattern;

public class Test {
	public static void main(String[] args) {
		String regex="\\w*\\.\\w*\\.\\w*\\.\\w*\\.\\w*\\([\\w\\W]*\\)";
		String str="com.study.test.HelloProxyImpl.helloProxy(String arg0)";
		System.out.println(Pattern.matches(regex, str));
		
		System.out.println(new NumberFormatException() instanceof Throwable);
	}
}
