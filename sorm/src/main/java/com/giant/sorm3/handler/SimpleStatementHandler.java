package com.giant.sorm3.handler;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.giant.commons.opeator.DBOperator;
import com.giant.sorm3.annotation.Delete;
import com.giant.sorm3.annotation.Insert;
import com.giant.sorm3.annotation.Select;
import com.giant.sorm3.annotation.Update;
import com.giant.sorm3.core.DBManger;
import com.giant.sorm3.utils.SormUtils;
import org.apache.log4j.Logger;

public class SimpleStatementHandler implements StatementHandler{
	private Logger logger=Logger.getLogger(SimpleStatementHandler.class);
	private Connection connection;
	private PreparedStatement pstmt;
	
	public SimpleStatementHandler(Connection connection) {
		super();
		this.connection = connection;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	@Override
	public PreparedStatement getPreparedStatement() {
		// TODO Auto-generated method stub
		return pstmt;
	}

	@Override
	public void setPreparedStatement(PreparedStatement pstmt) {
		// TODO Auto-generated method stub
		this.pstmt=pstmt;
	}
	
	@Override
	public ResultSet doSelect(Method method,Object[] paramValues) {
		String sql=method.getAnnotation(Select.class).value();
		sql=parseSql(sql);
		this.pstmt=DBOperator.getPreparedStatement(connection, sql, paramValues);
		return executeQuery(sql, pstmt, paramValues);
	}

	@Override
	public int doDML(Method method, Object[] paramValues) {
		// TODO Auto-generated method stub
		String sql=null;
		if(SormUtils.isInsertMethod(method)) {
			sql=method.getAnnotation(Insert.class).value();
		} else if(SormUtils.isDeleteMethod(method)) {
			sql=method.getAnnotation(Delete.class).value();
		} else if(SormUtils.isUpdateMethod(method)) {
			sql=method.getAnnotation(Update.class).value();
		}
		sql=parseSql(sql);
		PreparedStatement pstmt=DBOperator.getPreparedStatement(connection, sql,paramValues);
		return executeUpdate(sql, pstmt, paramValues);
	}
	
	@Override
	public int executeUpdate(String sql,PreparedStatement pstmt,Object[] paramValues) {
		int index=0;
		try {
			index=pstmt.executeUpdate();
			printInfo(sql, paramValues);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("执行SQL时出错:"+e.getMessage());
		}finally {
			DBManger.close(null, pstmt,null);
		}
		return index;
	}
	
	@Override
	public void printInfo(String sql,Object[] paramValues) {
		logger.debug("SQL语句 :"+sql);
		StringBuilder sb=new StringBuilder();
		sb.append("参数列表 :");
		if(paramValues!=null) {
			for(Object param:paramValues) {
				if(param!=null) {
					Class<?> clazz=param.getClass();
					sb.append(clazz.getSimpleName()+":"+param+"\t");
				}else {
					sb.append(param);
				}
			}
		}
		logger.debug(sb.toString());
	}
	@Override
	public ResultSet executeQuery(String sql,PreparedStatement pstmt,Object[] paramValues) {
		ResultSet rs=null;
		rs= DBOperator.getResultSet(pstmt);
		if(rs==null) {
			logger.info("执行SQL语句时出错");
		}else {
			printInfo(sql, paramValues);
		}
		return rs;
	}
	@Override
	public String parseSql(String sql) {
		StringBuilder sb=new StringBuilder();
		while(sql!=null&&!sql.equals("")) {
			if(SormUtils.hasParam(sql)) {
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

	

	
	
}
