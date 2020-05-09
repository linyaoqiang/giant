package com.giant.sorm3.convertor;

public class SqlServerTypeConvertor implements TypeConvertor {

    /**
     * @param columnType 数据库数据类型
     * @return Java数据类型
     */
    @Override
    public String dataBaseTypeToJavaType(String columnType) {
        return new MySqlTypeConvertor().dataBaseTypeToJavaType(columnType);
    }

    /**
     * @param javaType Java数据类型
     * @return 数据库数据类型
     */
    @Override
    public String javaTypeToDataBaseType(String javaType) {
        return null;
    }
}
