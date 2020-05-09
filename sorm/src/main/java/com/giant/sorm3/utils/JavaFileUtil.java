package com.giant.sorm3.utils;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.giant.commons.opeator.FileOperator;
import com.giant.commons.opeator.StringOperator;
import com.giant.sorm3.bean.ColumnInfo;
import com.giant.sorm3.bean.JavaFieldSetGet;
import com.giant.sorm3.bean.TableInfo;
import com.giant.sorm3.convertor.TypeConvertor;
import com.giant.sorm3.core.DBManger;

public class JavaFileUtil {
	/**
	 * 
	 * @param column    字段信息对象
	 * @param convertor 类型转化器
	 * @return 生成java字段信息
	 */
	public static JavaFieldSetGet createJavaFieldSetGet(ColumnInfo column, TypeConvertor convertor) {
		String javaType = convertor.dataBaseTypeToJavaType(column.getType());
		JavaFieldSetGet jfsg = new JavaFieldSetGet();
		StringBuilder sb = new StringBuilder();
		sb.append("\tprivate " + javaType + " " + column.getName() + ";").append("\n");
		jfsg.setFieldInfo(sb.toString());
		sb = new StringBuilder();
		sb.append("\tpublic void set" + StringOperator.firstToUpperCase(column.getName()) + "(" + javaType + " "
				+ column.getName() + "){\n");
		sb.append("\t\tthis." + column.getName() + "=" + column.getName() + ";\n");
		sb.append("\t}\n");
		jfsg.setSetInfo(sb.toString());
		sb = new StringBuilder();
		sb.append("\tpublic " + javaType + " get" + StringOperator.firstToUpperCase(column.getName()) + "(){\n");
		sb.append("\t\treturn this." + column.getName() + ";\n");
		sb.append("\t}\n");
		jfsg.setGetInfo(sb.toString());
		return jfsg;
	}

	/**
	 * 
	 * @param table     数据表信息
	 * @param convertor 类型转化器
	 * @return
	 */
	public static String createJavaSrc(TableInfo table, TypeConvertor convertor) {
		StringBuilder sb = new StringBuilder();
		sb.append("package " + DBManger.getPackage() + ";\n");
		sb.append("public class " + StringOperator.firstToUpperCase(table.getName()) + "{\n");
		List<JavaFieldSetGet> fields = new ArrayList<>();
		Map<String, ColumnInfo> columns = table.getColumns();
		for (ColumnInfo colum : columns.values()) {
			fields.add(createJavaFieldSetGet(colum, convertor));
		}
		for (JavaFieldSetGet field : fields) {
			sb.append(field.getFieldInfo());
		}
		for (JavaFieldSetGet getsetInfo : fields) {
			sb.append(getsetInfo.getGetInfo());
			sb.append(getsetInfo.getSetInfo());
		}
		sb.append("}");
		return sb.toString();
	}

	/**
	 * 
	 * @param srcByte java源文件字节数组
	 * @param srcPath 生成的路径
	 */
	public static void createJavaFile(byte[] srcByte, String srcPath) {
		try {
			FileOperator.in2Out(new ByteArrayInputStream(srcByte), new FileOutputStream(srcPath));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
