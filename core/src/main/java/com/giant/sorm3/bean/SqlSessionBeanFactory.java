package com.giant.sorm3.bean;


import com.giant.sorm3.session.SqlSession;
import com.giant.sorm3.session.SqlSessionFactory;

public class SqlSessionBeanFactory {
	private ThreadLocal<SqlSession> sqlSession=new ThreadLocal<SqlSession>();
	public synchronized SqlSession getSqlSession() {
		if(sqlSession.get()==null) {
			sqlSession.set(SqlSessionFactory.getThreadSqlSession());
		}
		return sqlSession.get();
	}
	
	public synchronized void close(SqlSession sqlSession) {
		sqlSession.close();
		this.sqlSession.set(null);
	}
}
