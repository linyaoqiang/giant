package com.giant.sorm3.handler;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;

import com.giant.sorm3.core.DBManger;
import com.giant.sorm3.exception.ParseParamException;
import org.apache.log4j.Logger;


public class SimpleExecutor implements Executor{
	private Logger logger=Logger.getLogger(SimpleExecutor.class);
	private ParameterHandler parameterHandler;
	private StatementHandler statementHandler;
	private ResultHandler resultHandler;
	
	private Connection connection;
	
	public SimpleExecutor(Connection connection) {
		this.connection=connection;
	}

	
	
	
	public SimpleExecutor(ParameterHandler parameterHandler, StatementHandler statementHandler,
			ResultHandler resultHandler, Connection connection) {
		super();
		this.parameterHandler = parameterHandler;
		this.statementHandler = statementHandler;
		this.resultHandler = resultHandler;
		this.connection = connection;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public ParameterHandler getParameterHandler() {
		return parameterHandler;
	}

	public void setParameterHandler(ParameterHandler parameterHandler) {
		this.parameterHandler = parameterHandler;
	}
	
	public ResultHandler getResultHandler() {
		return resultHandler;
	}



	public void setResultHandler(ResultHandler resultHandler) {
		this.resultHandler = resultHandler;
	}


	public StatementHandler getStatementHandler() {
		return statementHandler;
	}

	public void setStatementHandler(StatementHandler statementHandler) {
		this.statementHandler = statementHandler;
	}

	@Override
	public Object doSelect(Method method, Object[] args) {
		ResultSet rs=null;
		try {
			Object[] paramValues=parameterHandler.parseParameter(method, args);
			rs=statementHandler.doSelect(method, paramValues);
			return resultHandler.parseResultSet(method, rs);
		} catch (ParseParamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("解析SQL中发生错误，已经终止解析");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("解析结果集时发生错误!!!!");
		}finally {
			DBManger.close(rs,statementHandler.getPreparedStatement(),null);
			statementHandler.setPreparedStatement(null);
		}
		return null;
	}

	@Override
	public int doDML(Method method, Object[] args) {
		int index=0;
		try {
			Object[] paramValues = parameterHandler.parseParameter(method, args);
			index=statementHandler.doDML(method, paramValues);
		} catch (ParseParamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("解析中发生错误，已经终止解析");
		}
		
		return index;
	}




}
