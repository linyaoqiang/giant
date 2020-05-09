package com.giant.sorm3.bean;
import java.util.*;
/**
 * 
 * @author lyq
 * @version 1.0
 * 其对应数据里的一张表  
 */
public class TableInfo
{
	/**
	 * @name 表名
	 */
	private String name;
	/**
	 * 表中所有的字段对象   使用Map来存储
	 */
	private Map<String,ColumnInfo> columns;
	/**
	 * 表示唯一主键
	 */
	private ColumnInfo onlyKey;
	/**
	 * 表示联合主键 包括唯一主键
	 */
	private List<ColumnInfo> priKeys;

	public TableInfo(String name, Map<String, ColumnInfo> columns, ColumnInfo onlyKey, List<ColumnInfo> priKeys)
	{
		this.name = name;
		this.columns = columns;
		this.onlyKey = onlyKey;
		this.priKeys = priKeys;
	}
	public TableInfo(){
		
	}

	public void setPriKeys(List<ColumnInfo> priKeys)
	{
		this.priKeys = priKeys;
	}

	public List<ColumnInfo> getPriKeys()
	{
		return priKeys;
	}
	public Map<String, ColumnInfo> getColumns()
	{
		return columns;
	}

	public void setOnlyKey(ColumnInfo onlyKey)
	{
		this.onlyKey = onlyKey;
	}

	public ColumnInfo getOnlyKey()
	{
		return onlyKey;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setColumns(Map<String, ColumnInfo> columns)
	{
		this.columns = columns;
	}
}

