package com.giant.core;

import com.giant.aop.AopContext;
import com.giant.config.ApplicationConfiguration;
import org.apache.log4j.Logger;

/**
 * ApplicationContext 用于在应用程序加载时进行初始化，并组织控制反转和依赖注入
 * @author Administrator
 */
public class ApplicationContext extends AbstractBeanFactory{
	private Logger logger=Logger.getLogger(ApplicationContext.class);

	private ApplicationConfiguration configuration;

	public ApplicationContext(ApplicationConfiguration configuration, AopContext aopContext) {
		super(configuration,aopContext);
		this.configuration=configuration;
	}
}
