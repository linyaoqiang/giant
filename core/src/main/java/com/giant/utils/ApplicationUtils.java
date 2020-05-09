package com.giant.utils;


import com.giant.core.ApplicationContext;

/**
 * 
 * @author Administrator
 *	应用程序上下文工具，放置Application对象
 */
public class ApplicationUtils {
	/**
	 * application对象
	 */
	private static ApplicationContext application;
	/**
	 * 
	 * @param application 应用程序上下文
	 * 	设置applcation对象
	 */
	public static void setApplication(ApplicationContext application) {
		ApplicationUtils.application=application;
	}
	/**
	 * 
	 * @return 应用程序上下文
	 * 	获取application对象
	 */
	public static ApplicationContext getApplication() {
		return application;
	}
}
