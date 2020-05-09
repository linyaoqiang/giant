package com.giant.commons.loader;

import com.giant.commons.opeator.FileOperator;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


/**
 * 文件 类加载器
 */
public class FileClassLoader extends ClassLoader {
	protected String path;

	/**
	 * 
	 * @param path 指定文件或者项目路径
	 */
	public FileClassLoader(String path) {
		this.path = path;
	}

	@Override
	protected Class<?> findClass(String className) {
		try {
			Class<?> c = findLoadedClass(className);
			if (c != null)
				return c;
			ClassLoader parent = getParent();
			c = parent.loadClass(className);
			if (c != null)
				return c;
			byte[] classByte = getClassByte(className);
			return c = defineClass(className, classByte, 0, classByte.length);
		} catch(Exception e) {
			
		}
		return null;
	}

	
	/**
	 * 
	 * @param className 需要转化成字节的Class文件 例如com.study.HelloWorld 前面是包名要求加上
	 * @return 字节数组
	 */
	protected byte[] getClassByte(String className) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String classPath = getClassPath(className);
		try {
			FileOperator.in2Out(new FileInputStream(classPath), baos);
			return baos.toByteArray();
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	/**
	 * 
	 * @param className Class文件 例如com.study.HelloWorld 前面是包名要求加上
	 * @return 返回该文件绝对路径
	 */
	protected String getClassPath(String className) {
		return path + className.replace(".", "/") + ".class";
	}
}
