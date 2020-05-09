package com.giant.sorm3.handler;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface StatementHandler {
	ResultSet doSelect(Method method, Object[] paramValues);
	int doDML(Method method, Object[] paramValues);
	PreparedStatement getPreparedStatement();
	void setPreparedStatement(PreparedStatement pstmt);

	int executeUpdate(String sql, PreparedStatement pstmt, Object[] paramValues);

	void printInfo(String sql, Object[] paramValues);

	ResultSet executeQuery(String sql, PreparedStatement pstmt, Object[] paramValues);

	String parseSql(String sql);
}
