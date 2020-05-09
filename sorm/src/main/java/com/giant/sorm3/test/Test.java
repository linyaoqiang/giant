package com.giant.sorm3.test;

import com.giant.sorm3.exception.ParseParamException;
import com.giant.sorm3.handler.SimpleExecutor;
import com.giant.sorm3.session.SqlSession;
import com.giant.sorm3.session.SqlSessionFactory;
import com.giant.sorm3.utils.SormUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Test {
	private List<String> names;

	public Map<String, SimpleExecutor> test(List<String> list){
		return null;

	}
	public String parseSql(String sql) {
		StringBuilder sb=new StringBuilder();
		while(sql!=null&&!sql.equals("")) {
			if(sql.contains("#{")&&sql.contains("}")&&sql.indexOf("#{")<sql.indexOf("}")) {
				sb.append(sql.substring(0,sql.indexOf("#{")));
				sb.append("?");
				sql=sql.substring(sql.indexOf("#{")+2);
				sql=sql.substring(sql.indexOf("}")+1);
			}else {
				sb.append(sql);
				sql=null;
			}
		}
		return sb.toString();
	}
	
	public List<String> parseSqlParam(String sql) throws ParseParamException {
		if(sql==null||sql.equals("")) {
			return null;
		}
		List<String> list=new ArrayList<String>();
		while(SormUtils.hasParam(sql)) {
			
			sql=sql.substring(sql.indexOf("#{")+2);
			String paramName=sql.substring(0,sql.indexOf("}"));
			sql=sql.substring(sql.indexOf("}")+1);
			list.add(paramName);
		}
		return list;
	}
	public static void testMap(Object[] args) {
		for(Object object:args) {
			Class<?> clazz=object.getClass();
			if(clazz.equals(Map.class)||HashMap.class.equals(clazz)) {
				System.out.println(true);
			}
		}
	}
}
