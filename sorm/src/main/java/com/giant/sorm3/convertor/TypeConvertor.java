package com.giant.sorm3.convertor;

public interface TypeConvertor
{
	/**
	 * 
	 * @param columnType 数据库数据类型
	 * @return  Java数据类型
	 */
	String dataBaseTypeToJavaType(String columnType);
	/**
	 * 
	 * @param javaType Java数据类型
	 * @return	数据库数据类型
	 */
	String javaTypeToDataBaseType(String javaType);
}

