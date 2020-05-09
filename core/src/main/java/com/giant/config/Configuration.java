package com.giant.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import com.giant.aop.bean.AOPStandardSpace;
import com.giant.aop.config.AOPConfiguration;
import com.giant.commons.opeator.FileOperator;
import org.apache.log4j.Logger;

/**
 * 	应用程序上下文配置对象
 * @author Administrator
 *
 */
public class Configuration {
	private Logger logger=Logger.getLogger(Configuration.class);
	/**
	 * 	配置文件名称
	 */
	private String configFile;
	/**
	 * 	存放需要扫描包名的集合
	 */
	private List<String> packages;
	/**
	 * 	存放需要被加载的类的全限定路径的集合
	 */
	private List<String> classes;
	/**
	 * 	存放了包名于物理地址的映射
	 */
	private Map<String,String> packagePathMapping;
	
	private AOPConfiguration aopConfiguration;
	/**
	 * 	packages配置名
	 */
	private static final String PACKAGES="scan-packages";
	/**
	 * 	classes配置名
	 */
	private static final String CLASSES="scan-classes";
	public List<String> getPackages() {
		return packages;
	}
	public void setPackages(List<String> packages) {
		this.packages = packages;
	}
	public List<String> getClasses() {
		return classes;
	}
	public void setClasses(List<String> classes) {
		this.classes = classes;
	}
	public String getConfigFile() {
		return configFile;
	}
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
	/**
	 * 	带配置文件路径的构造方法
	 * 	
	 * @param configFile 配置文件
	 *  	构造应用程序上下文配置文件
	 */
	public Configuration(String configFile) {
		super();
		this.configFile = configFile;
		initByProperties();
	}
	/**
	 * 	组织初始化，加载映射等
	 */
	private void initByProperties() {
		// TODO Auto-generated method stub
			Properties properties= FileOperator.createProperties(configFile);
			initPackages(properties);
			initClasses(properties);
			if(this.packages!=null||this.classes!=null) {
				initPackageUrl();
				initAop(properties);
				logger.debug("初始化配置成功");
			}else {
				logger.debug("初始化配置失败:没有任何东西需要创建对象");
			}

		
	}
	public void initAop(Properties properties) {
		// TODO Auto-generated method stub
		String status=properties.getProperty(AOPStandardSpace.AOP_STATUS);
		if(status!=null&&Boolean.parseBoolean(status)) {
			this.aopConfiguration=new AOPConfiguration();
			aopConfiguration.setStatus(true);
		}
	}
	public void initPackages(Properties properties) {
		String packages=properties.getProperty(PACKAGES);
		if(packages!=null) {
			if(packages.contains(",")) {
				String[] packageArray=packages.split(",");
				this.packages=new CopyOnWriteArrayList<String>(packageArray);
			}else {
				this.packages=new CopyOnWriteArrayList<String>();
				this.packages.add(packages);
			}
			logger.debug("初始化配置  "+ PACKAGES  +" 成功:"+this.packages);
		}else {
			logger.error("初始化配置错误, 没有任何包可以扫描");
		}
	}
	
	
	public void initClasses(Properties properties) {
		String classes=properties.getProperty(CLASSES);
		if(classes!=null) {
			if(classes.contains(",")) {
				this.classes=new CopyOnWriteArrayList<String>(classes.split(","));
			}else {
				this.classes=new CopyOnWriteArrayList<String>();
				this.classes.add(classes);
			}
			logger.debug("初始化配置  "+CLASSES+" 成功:"+this.classes);
			
		}else {
			logger.error("初始化配置错误, 没有任何类可以扫描");
		}
	}
	
	/**
	 * 	无参的构造方法，使用默认配置文件名称	application.properties
	 */
	public Configuration() {
		this("application.properties");
	}
	/**
	 * 	初始化包名于物理地址映射
	 */
	private void initPackageUrl() {
		packagePathMapping=new HashMap<String, String>();
		for(String packageName:packages) {
			String	packageUrl=packageName.replace(".","/");
			packagePathMapping.put(packageName,packageUrl);
		}
	}
	public Map<String, String> getPackagePathMapping() {
		return packagePathMapping;
	}
	public void setPackagePathMapping(Map<String, String> packagePathMapping) {
		this.packagePathMapping = packagePathMapping;
	}
	public AOPConfiguration getAopConfiguration() {
		return aopConfiguration;
	}
	public void setAopConfiguration(AOPConfiguration aopConfiguration) {
		this.aopConfiguration = aopConfiguration;
	}
	
	
}
