package com.giant.sorm3.session;

import java.sql.SQLException;

import com.giant.sorm3.handler.MapperHandler;

public interface SqlSession {
	void commit() throws SQLException;
	void rollback() throws SQLException;
	void setAutoCommit(boolean flag) throws SQLException;
	<E> E getMapper(Class<E> clazz);
	void close();
	void setMapperHandler(MapperHandler mapperHandler);
}
