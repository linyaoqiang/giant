package com.giant.sorm3.pool;
import com.giant.commons.opeator.DBOperator;
import com.giant.sorm3.core.DBManger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
/**
 * 
 * @author lyq
 * @version 1.0
 * 连接池,将数据库链接放进这里,可以大量节省创建大量套接字的时间
 * 三次握手在不考虑延迟的情况大概需要0.2秒
 */
public class DBConnectionPool
{
	private  List<Connection> connections;
	
	private static final int POOL_MIN_SIZE= DBManger.getConfiguration().getPoolMinSize();
	
	private static final int POOL_MAX_SIZE=DBManger.getConfiguration().getPoolMaxSize();
	
	private static final int POOL_MAX_ACTIVE=DBManger.getConfiguration().getPoolMaxActive();
	
	private static final boolean AUTOCOMMIT=DBManger.getConfiguration().isPoolAutoCommit();
	
	private int activeConnection=0;
	/**
	 * 	初始化数据库连接池
	 * 	在创建该对象调用该方法
	 * @throws SQLException 
	 */
	public synchronized void init() throws SQLException{
		if(connections==null){
			connections=new CopyOnWriteArrayList<>();
		}
		while(connections.size()<POOL_MIN_SIZE){
			Connection conn= DBManger.createConnection();
			conn.setAutoCommit(AUTOCOMMIT);
			connections.add(conn);
		}
	}
	
	public synchronized Connection getConnection() throws SQLException, InterruptedException{
		if(activeConnection>=POOL_MAX_ACTIVE) {
			this.wait();
		}
		if(connections.size()<=0){
			init();
		}
		int last_index=connections.size()-1;
		Connection conn= connections.get(last_index);
		connections.remove(last_index);
		activeConnection++;
		return conn;
	}
	public synchronized void returnConnection(Connection conn) throws SQLException{
		if(connections.size()<POOL_MAX_SIZE){
			if(conn!=null) {
				connections.add(conn);
				conn.setAutoCommit(AUTOCOMMIT);
			}
		}else{
			DBOperator.closeJDBC(null,null,conn);
		}
		if(conn!=null) {
			activeConnection--;
			this.notify();
		}
	}
	public DBConnectionPool() throws SQLException{
		init();
	}
}


