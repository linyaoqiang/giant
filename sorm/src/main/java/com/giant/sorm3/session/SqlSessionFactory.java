package com.giant.sorm3.session;

import java.sql.Connection;

import com.giant.sorm3.bean.StandardSpace;
import com.giant.sorm3.core.DBManger;
import com.giant.sorm3.handler.*;

public class SqlSessionFactory {
	private static ThreadLocal<SqlSession> session=new ThreadLocal<SqlSession>();
	public static SqlSession createSqlSession() {
		Connection connection= DBManger.getConnection();
		SqlSession sqlSession=new SimpleSqlSession(connection);
		Executor executor = null;
		MapperHandler mapperHandler=null;
		if(DBManger.getConfiguration().isUseXMLMapper()){
			executor = createXMLExecutor(connection,sqlSession);
			mapperHandler = createXMLMapperHandler(executor);
		}else{
			executor = createExecutor(connection,sqlSession);
			mapperHandler=createMapperHandler(executor);
		}


		sqlSession.setMapperHandler(mapperHandler);
		return new SimpleSqlSession(connection, mapperHandler);
		
	}
	public synchronized static SqlSession getThreadSqlSession() {
		SqlSession sqlSession=session.get();
		if(sqlSession==null) {
			session.set(createSqlSession());
			sqlSession=session.get();
		}
		return sqlSession;
	}
	
	private static  Executor createExecutor(Connection connection,SqlSession sqlSession) {
		String resultModel=DBManger.getConfiguration().getResultHandler();
		ParameterHandler parameterHandler=new SimpleParameterHandler();
		StatementHandler statementHandler=new SimpleStatementHandler(connection);
		ResultHandler resultHandler=null;
		if(StandardSpace.HANDLER_RESULT_COLUMN.equals(resultModel)) {
			resultHandler=new ColumnResultHandler(sqlSession);
		}else {
			resultHandler=new SimpleResultHandler();
		}
		
		Executor executor=new SimpleExecutor(parameterHandler, statementHandler,
				resultHandler, connection);
		return executor;
		
	}

	private static Executor createXMLExecutor(Connection connection,SqlSession sqlSession){

		ParameterHandler parameterHandler=new XMLParameterHandler();
		StatementHandler statementHandler=new XMLStatementHandler(connection);
		ResultHandler resultHandler = createResultHandler(sqlSession);

		Executor executor=new XMLExecutor(parameterHandler, statementHandler,
				resultHandler, connection);
		return executor;
	}
	
	private static MapperHandler createMapperHandler(Executor executor) {
		return new MapperHandler(executor);
	}
	private static MapperHandler createXMLMapperHandler(Executor executor){
		return new XMLMapperHandler(executor);
	}

	public static ResultHandler createResultHandler(SqlSession sqlSession){
		String resultModel=DBManger.getConfiguration().getResultHandler();
		ResultHandler resultHandler=null;
		if(StandardSpace.HANDLER_RESULT_COLUMN.equals(resultModel)) {
			resultHandler=new ColumnResultHandler(sqlSession);
		}else {
			resultHandler=new SimpleResultHandler();
		}
		return resultHandler;
	}
}
