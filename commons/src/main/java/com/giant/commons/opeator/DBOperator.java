package com.giant.commons.opeator;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBOperator {
	/**
	 * 
	 * @param driver   驱动类
	 * @param URL      链接
	 * @param username 用户名
	 * @param password 密码
	 * @return 连接数据库的对象Connection
	 */
	public static Connection getConnection(String driver, String URL, String username, String password) {
		try {
			Class.forName(driver);
			return DriverManager.getConnection(URL, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param rs   结果集
	 * @param stmt 查询对象
	 * @param conn 连接 用于关闭数据库
	 */
	public static void closeJDBC(ResultSet rs, Statement stmt, Connection conn) {
		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
		}
		try {
			if (stmt != null)
				stmt.close();
		} catch (SQLException e) {
		}
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param rs   结果集
	 * @param stmt 查询对象 重载
	 */
	public static void closeJDBC(ResultSet rs, Statement stmt) {
		closeJDBC(rs, stmt, null);
	}

	/**
	 * 
	 * @param conn  Connection
	 * @param sql   sql语句
	 * @param parms 占位符参数
	 * @return 返回已经填好参数的PreparedStatement
	 */
	public static PreparedStatement getPreparedStatement(Connection conn, String sql, Object... parms) {
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			loadParms(ps, parms);
			return ps;
		} catch (SQLException e) {
		}
		return null;
	}

	/**
	 * 
	 * @param pstmt PreparedStatement
	 * @param parms 执行的占位符参数
	 * 
	 */
	public static void loadParms(PreparedStatement pstmt, Object[] parms) {
		if (parms != null && pstmt != null) {
			for (int i = 0; i < parms.length; i++) {
				try {
					pstmt.setObject(i + 1, parms[i]);
				} catch (SQLException e) {
				}
			}
		}
	}

	/**
	 * 
	 * @param stmt 查询对象
	 * @param sql  SQL语句
	 * @return 结果集ResultSet
	 */
	public static ResultSet getResultSet(Statement stmt, String sql) {
		try {
			PreparedStatement pstmt = null;
			if (stmt instanceof PreparedStatement) {
				pstmt = (PreparedStatement) stmt;
				return pstmt.executeQuery();
			}
			return stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param pstmt PreparedStatement
	 * @return 结果集ResultSet
	 */
	public static ResultSet getResultSet(PreparedStatement pstmt) {
		return getResultSet(pstmt, null);
	}
	/**
	 * 
	 * @param rs       结果集
	 * @param clumname 字段名
	 * @return 如果字段名对应的数据类型是clob 可以转化成一个字符流
	 */
	public static Reader readerforCLOB(ResultSet rs, String columnname) {
		try {
			return rs.getClob(columnname).getCharacterStream();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * @param rs       结果集
	 * @param clumname 字段名
	 * @return 如果字段名对应的数据类型是Blob 可以转化成一个字节流
	 */
	public static InputStream streamforBLOB(ResultSet rs, String clumname) {
		try {
			return rs.getBlob(clumname).getBinaryStream();
		} catch (Exception e) {
		}
		return null;
	}
}
