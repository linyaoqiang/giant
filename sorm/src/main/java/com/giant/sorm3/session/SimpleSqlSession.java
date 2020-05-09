package com.giant.sorm3.session;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

import com.giant.sorm3.core.DBManger;
import com.giant.sorm3.handler.MapperHandler;


public class SimpleSqlSession implements SqlSession{
	private Connection connection;
	private MapperHandler mapperHandler;


	public SimpleSqlSession(Connection connection,MapperHandler mapperHandler) {
		super();
		this.connection = connection;
		this.mapperHandler=mapperHandler;
	}

	
	
	public SimpleSqlSession(Connection connection) {
		super();
		// TODO Auto-generated constructor stub
		this.connection=connection;
	}

	public MapperHandler getMapperHandler() {
		return mapperHandler;
	}



	public void setMapperHandler(MapperHandler mapperHandler) {
		this.mapperHandler = mapperHandler;
	}



	@Override
	public void commit() throws SQLException {
		synchronized (SqlSession.class) {
			connection.commit();
		}
	}

	@Override
	public void rollback() throws SQLException {
		synchronized (SqlSession.class) {
			connection.rollback();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E getMapper(Class<E> clazz) {
		// TODO Auto-generated method stub
		E e=(E)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),new Class[] {clazz},mapperHandler);
		return e;
	}

	@Override
	public void close() {
		DBManger.close(null,null, connection);
	}

	@Override
	public void setAutoCommit(boolean flag) throws SQLException{
		this.connection.setAutoCommit(flag);
		
	}
}
