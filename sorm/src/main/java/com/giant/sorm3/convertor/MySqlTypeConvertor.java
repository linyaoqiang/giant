package com.giant.sorm3.convertor;

public class MySqlTypeConvertor implements TypeConvertor {

    @Override
    public String dataBaseTypeToJavaType(String columnType) {
        columnType = columnType.toLowerCase();
        if (columnType.contains("char")) {
            return "String";
        } else if (columnType.contains("int") && !columnType.equals("bigint")) {
            return "Integer";
        } else if (columnType.equals("bigint")) {
            return "Long";
        } else if (columnType.equals("date")) {
            return "java.sql.Date";
        } else if (columnType.equals("timestamp") || columnType.equals("datetime")) {
            return "java.sql.Timestamp";
        } else if (columnType.equals("year")) {
            return "java.time.Year";
        } else if (columnType.equals("time")) {
            return "java.sql.Time";
        } else if (columnType.equals("text")) {
            return "java.sql.Clob";
        } else if (columnType.equals("blob")) {
            return "java.sql.Blob";
        }
        return null;
    }

    @Override
    public String javaTypeToDataBaseType(String javaType) {
        // TODO: Implement this method
        return null;
    }

}

