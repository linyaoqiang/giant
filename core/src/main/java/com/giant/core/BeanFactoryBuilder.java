package com.giant.core;

import com.giant.aop.AopContext;
import com.giant.config.ApplicationConfiguration;

/**
 * BeanFactory生成器
 */
public class BeanFactoryBuilder {
    public BeanFactory build(String config){
        //获取应用程序上下文配置
        ApplicationConfiguration configuration = new ApplicationConfiguration(config);
        //获取Aop上下文
        AopContext aopContext = new AopContext(config);
        //返回应用程序上下文
        return new ApplicationContext(configuration,aopContext);
    }
}
