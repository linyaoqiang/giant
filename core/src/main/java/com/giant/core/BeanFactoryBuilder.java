package com.giant.core;

import com.giant.aop.AopContext;
import com.giant.config.ApplicationConfiguration;

public class BeanFactoryBuilder {
    public BeanFactory build(String config){
        ApplicationConfiguration configuration = new ApplicationConfiguration(config);
        AopContext aopContext = new AopContext(config);
        return new ApplicationContext(configuration,aopContext);
    }
}
