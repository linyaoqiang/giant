package com.giant.sorm3.core;

import com.giant.commons.opeator.DBOperator;
import com.giant.commons.opeator.StringOperator;
import com.giant.sorm3.bean.ColumnInfo;
import com.giant.sorm3.bean.TableInfo;
import com.giant.sorm3.convertor.TypeConvertor;
import com.giant.sorm3.utils.JavaFileUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author lyq
 * @version 1.0 数据库表的相关信息 上下文
 */
public class TableContext {
	/**
	 * 存放数据表信息
	 */
	private static Map<String, TableInfo> tables = new HashMap<>();
	/**
	 * 存放数据表与对应的class 对象
	 */
	private static Map<Class<?>, TableInfo> poClasss = new HashMap<>();
	/**
	 * 初始化
	 */
	static {
		createTableInfo();
		loadJavaBeanClassByDataBase();
	}

	public static Map<String, TableInfo> getTables() {
		return tables;
	}

	public static Map<Class<?>, TableInfo> getPoClass() {
		return poClasss;
	}

	/**
	 * 从数据库中加载表信息 方便映射成java源文件
	 * 
	 * @return 数据表信息
	 */
	public static Map<String, TableInfo> createTableInfo() {
		try {
			Connection conn = DBManger.getConnection();
			DatabaseMetaData dbmd = conn.getMetaData();
			ResultSet rs = dbmd.getTables(null, "%", "%", new String[] { "TABLE" });
			while (rs.next()) {
				TableInfo table = new TableInfo();
				String tableName = rs.getString("TABLE_NAME");
				table.setName(tableName.toLowerCase());
				tables.put(tableName.toLowerCase(), table);
				ResultSet set1 = dbmd.getColumns(null, "%", tableName, "%");
				Map<String, ColumnInfo> columns = new HashMap<>();
				while (set1.next()) {
					ColumnInfo column = new ColumnInfo();
					column.setName(set1.getString("COLUMN_NAME").toLowerCase());
					column.setType(set1.getString("TYPE_NAME"));
					column.setPriKey(0);
					columns.put(column.getName(), column);
				}
				ResultSet set2 = dbmd.getPrimaryKeys(null, null, tableName);
				List<ColumnInfo> priKeys = new ArrayList<>();
				while (set2.next()) {
					ColumnInfo column = new ColumnInfo();
					column.setName(set2.getString("COLUMN_NAME").toLowerCase());
					// column.setType(set2.getString("TYPE_NAME"));
					column.setPriKey(1);
					priKeys.add(column);
				}
				if (priKeys.size() > 0) {
					table.setOnlyKey(priKeys.get(0));
				}
				table.setPriKeys(priKeys);
				table.setColumns(columns);
				DBOperator.closeJDBC(set1, null);
				DBOperator.closeJDBC(set2, null);
			}
			DBManger.close(rs, null, conn);
			return tables;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 更新Java源文件到指定目录
	 * 
	 * @param convertor
	 */
	public static void updateJavaFileByDataBase(TypeConvertor convertor) {
		for (TableInfo table : tables.values()) {
			String src = JavaFileUtil.createJavaSrc(table, convertor);
			String path = DBManger.getSrcPath() + DBManger.getPackage().replace(".", "/");
			File pathFile = new File(path);
			if (!pathFile.exists()) {
				pathFile.mkdirs();
			}
			JavaFileUtil.createJavaFile(src.getBytes(),
					path + "/" + StringOperator.firstToUpperCase(table.getName()) + ".java");
		}

	}

	/**
	 * 加载所需要的实体类 初始化poClass
	 */
	public static void loadJavaBeanClassByDataBase() {
		for (TableInfo table : tables.values()) {
			try {
				Class<?> clazz = Class
						.forName(DBManger.getPackage() + "." + StringOperator.firstToUpperCase(table.getName()));
				poClasss.put(clazz, table);
			} catch (ClassNotFoundException e) {
				System.err.println("请在文件更新后刷新,然后就可以正常运行了!");
				break;
			}
		}
	}
}
