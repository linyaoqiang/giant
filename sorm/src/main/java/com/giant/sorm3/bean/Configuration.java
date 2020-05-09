package com.giant.sorm3.bean;

/**
 * 
 * @author lyq
 * @version 1.0 配置文件对象，用于存储配置信息
 */
public class Configuration {
	private String driver;
	private String url;
	private String username;
	private String password;
	private String poolDb;
	private String srcPath;
	private String srcPackage;


	private int poolMinSize;
	private int poolMaxSize;
	private int poolMaxActive;
	private boolean poolAutoCommit;
	private String resultHandler;


	private boolean useXMLMapper;
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPoolDb() {
		return poolDb;
	}
	public void setPoolDb(String poolDb) {
		this.poolDb = poolDb;
	}
	public String getSrcPath() {
		return srcPath;
	}
	public void setSrcPath(String srcPath) {
		this.srcPath = srcPath;
	}
	public String getSrcPackage() {
		return srcPackage;
	}
	public void setSrcPackage(String srcPackage) {
		this.srcPackage = srcPackage;
	}
	public int getPoolMinSize() {
		return poolMinSize;
	}
	public void setPoolMinSize(int poolMinSize) {
		this.poolMinSize = poolMinSize;
	}
	public int getPoolMaxSize() {
		return poolMaxSize;
	}
	public void setPoolMaxSize(int poolMaxSize) {
		this.poolMaxSize = poolMaxSize;
	}
	public int getPoolMaxActive() {
		return poolMaxActive;
	}
	public void setPoolMaxActive(int poolMaxActive) {
		this.poolMaxActive = poolMaxActive;
	}
	public boolean isPoolAutoCommit() {
		return poolAutoCommit;
	}
	public void setPoolAutoCommit(boolean poolAutoCommit) {
		this.poolAutoCommit = poolAutoCommit;
	}
	public String getResultHandler() {
		return resultHandler;
	}
	public void setResultHandler(String resultHandler) {
		this.resultHandler = resultHandler;
	}

	public boolean isUseXMLMapper() {
		return useXMLMapper;
	}

	public void setUseXMLMapper(boolean useXMLMapper) {
		this.useXMLMapper = useXMLMapper;
	}
}
