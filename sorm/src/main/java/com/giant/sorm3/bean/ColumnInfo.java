package com.giant.sorm3.bean;

/**
 * @author lyq
 * @version 1.0
 * 该类用于表示数据表中的一个字段
 */
public class ColumnInfo {

    private String name;
    private String type;
    private int priKey;

    public ColumnInfo(String name, String type, int priKey) {
        this.name = name;
        this.type = type;
        this.priKey = priKey;
    }

    public ColumnInfo() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setPriKey(int priKey) {
        this.priKey = priKey;
    }

    public int getPriKey() {
        return priKey;
    }
}

