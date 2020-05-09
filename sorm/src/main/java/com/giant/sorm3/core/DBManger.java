package com.giant.sorm3.core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.giant.commons.opeator.DBOperator;
import com.giant.sorm3.bean.Configuration;
import com.giant.sorm3.bean.StandardSpace;
import com.giant.sorm3.convertor.MySqlTypeConvertor;
import com.giant.sorm3.convertor.OracleTypeConvertor;
import com.giant.sorm3.convertor.SqlServerTypeConvertor;
import com.giant.sorm3.convertor.TypeConvertor;
import com.giant.sorm3.pool.DBConnectionPool;
import org.apache.log4j.Logger;


/**
 * 
 * @author lyq
 * @version 1.0 数据库链接管理员
 *
 */
public class DBManger {
	private static Logger logger=Logger.getLogger(DBManger.class);
	/**
	 * 用于存放配置信息对象和连接池对象
	 */
	private static Configuration cfgt;
	private static DBConnectionPool pool;
	/**
	 * 初始化
	 */
	static {
		try {
			/**
			 * p对象用于读取配置信息
			 */
			Properties p = new Properties();
			/**
			 * 当前线程的类加载器
			 */
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			/**
			 * 在当前构建目录下找到db.properties gerResorceAsStream该方法返回一个InputStream流对象 Properties
			 * load能够从流中读取到配置信息
			 */
			p.load(loader.getResourceAsStream("db.properties"));
			/**
			 * 设置配置信息对象
			 */
			cfgt = new Configuration();
			cfgt.setDriver(p.getProperty(StandardSpace.DRIVER));
			cfgt.setUrl(p.getProperty(StandardSpace.URL));
			cfgt.setUsername(p.getProperty(StandardSpace.USERNAME));
			cfgt.setPassword(p.getProperty(StandardSpace.PASSWORD));
			cfgt.setSrcPath(p.getProperty(StandardSpace.SRC_PATH));
			cfgt.setSrcPackage(p.getProperty(StandardSpace.SRC_PACKAGE));
			cfgt.setPoolDb(p.getProperty(StandardSpace.POOL_DB));
			String minSize=p.getProperty(StandardSpace.POOL_MIN_SIZE);
			cfgt.setPoolMinSize(Integer.parseInt(minSize));
			String maxSize=p.getProperty(StandardSpace.POOL_MAX_SIZE);
			cfgt.setPoolMaxSize(Integer.parseInt(maxSize));
			String maxActive=p.getProperty(StandardSpace.POOL_MAX_ACTIVE);
			cfgt.setPoolMaxActive(Integer.parseInt(maxActive));
			
			cfgt.setResultHandler(p.getProperty(StandardSpace.HANDLER_RESULT));

			cfgt.setUseXMLMapper(Boolean.parseBoolean(p.getProperty(StandardSpace.MAPPER_USE_XML)));
			/**
			 * 初始化连接池
			 */
			pool = new DBConnectionPool();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return 创建一个链接
	 */
	public static Connection createConnection() {
		return DBOperator.getConnection(cfgt.getDriver(), cfgt.getUrl(), cfgt.getUsername(),cfgt.getPassword());
	}

	/**
	 * 
	 * @return 从连接池中取出一个链接
	 */
	public static Connection getConnection() {
		try {
			return pool.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("无法设置提交方式!"+e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.error("使当前线程暂停使发生错误"+e.getMessage());
		}
		return null;
	}

	/**
	 * 
	 * @return 返回配置信息对象
	 */
	public static Configuration getConfiguration() {
		return cfgt;
	}

	/**
	 * 
	 * @return 返回当前正在使用的数据库的String对象
	 */
	public static String dataBaseInUse() {
		return cfgt.getPoolDb();
	}

	/**
	 * 
	 * @return 返回创建的Java源文件的包名
	 */
	public static String getPackage() {
		return cfgt.getSrcPackage();
	}

	/**
	 * 
	 * @return 返回创建的Java源文件项目路径
	 */
	public static String getSrcPath() {
		return cfgt.getSrcPath();
	}

	/**
	 * 
	 * @return 根据配置信息返回数据库类型转化对象
	 */
	public static TypeConvertor returnTypeConvertor() {
		String dbName = dataBaseInUse().toLowerCase();
		if (dbName.equals("mysql")) {
			return new MySqlTypeConvertor();
		} else if (dbName.equals("sqlserver")) {
			return new SqlServerTypeConvertor();
		} else if (dbName.equals("oracle")) {
			return new OracleTypeConvertor();
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param rs    要关闭的结果集
	 * @param pstmt 将要关闭的PreparedStatement
	 * @param conn  将数据库链接返回连接池,也可能是关闭,在于配置信息的连接数参数
	 */
	public static void close(ResultSet rs, PreparedStatement pstmt, Connection conn) {
		DBOperator.closeJDBC(rs, pstmt);
		try {
			pool.returnConnection(conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("执行返回或者关闭，设置提交方式时出错"+e.getMessage());
		}
	}
}
