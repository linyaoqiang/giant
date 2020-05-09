package com.giant.sorm3.bean;
/**
 * 
 * @author lyq
 * @version 1.0
 *
 */
public class JavaFieldSetGet
{
	/**
	 * @serialField 对应数据库一张表中的一个字段，通过数据库类型转化类以及生成Java源文件中所需要的一些引用信息,set,get方法
	 */
	private String fieldInfo;
	private String getInfo;
	private String setInfo;

	public JavaFieldSetGet(String fieldInfo, String getInfo, String setInfo)
	{
		this.fieldInfo = fieldInfo;
		this.getInfo = getInfo;
		this.setInfo = setInfo;
	}
	public JavaFieldSetGet(){
		
	}

	public void setFieldInfo(String fieldInfo)
	{
		this.fieldInfo = fieldInfo;
	}

	public String getFieldInfo()
	{
		return fieldInfo;
	}

	public void setGetInfo(String getInfo)
	{
		this.getInfo = getInfo;
	}

	public String getGetInfo()
	{
		return getInfo;
	}

	public void setSetInfo(String setInfo)
	{
		this.setInfo = setInfo;
	}

	public String getSetInfo()
	{
		return setInfo;
	}

	@Override
	public String toString()
	{
		// TODO: Implement this method
		return fieldInfo+"\n"+getInfo+"\n"+setInfo+"\n";
	}
	
}

