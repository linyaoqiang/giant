package com.giant.core;

import com.giant.bean.BeanInformation;
import com.giant.bean.ConstructorArg;
import com.giant.config.ApplicationConfiguration;
import com.giant.exception.CreateBeanException;

import java.lang.reflect.InvocationTargetException;

/**
 * 创建模板
 */
public interface CreateTemplate {
    Object create(ApplicationConfiguration configuration, BeanInformation information, ConstructorArg arg) throws ClassNotFoundException, CreateBeanException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException;
}
