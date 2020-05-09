package com.giant.commons.loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import com.giant.commons.opeator.FileOperator;

/**
 * 
 * @author lyq
 * @version 1.0 url 类加载器
 */
public class URLClassLoader extends FileClassLoader {
	/**
	 * 
	 * @param path url路径 必要时加上协议
	 */
	public URLClassLoader(String path) {
		super(path);
	}

	/**
	 * 获得字节码文件的字节数组
	 */
	@Override
	protected byte[] getClassByte(String className) {
		String classPath = getClassPath(className);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			FileOperator.in2Out(new URL(classPath).openConnection().getInputStream(), baos);
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return super.getClassByte(className);
	}

}
