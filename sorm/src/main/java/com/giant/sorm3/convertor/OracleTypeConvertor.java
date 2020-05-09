package com.giant.sorm3.convertor;

public class OracleTypeConvertor implements TypeConvertor {
    TypeConvertor convertor=new MySqlTypeConvertor();
    @Override
    public String dataBaseTypeToJavaType(String columnType) {
        columnType=columnType.toLowerCase();
        if(columnType.equals("number")) {
            return "Number";
        }else if(columnType.startsWith("varchar")){
            return "String";
        }else if(columnType.equals("date")){
            return "java.sql.Date";
        }else{
           return convertor.dataBaseTypeToJavaType(columnType);
        }
    }
    @Override
    public String javaTypeToDataBaseType(String javaType) {
        return null;
    }
}
