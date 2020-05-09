package com.giant.aop.interceptor;

import java.lang.reflect.Method;

/**
 * 方法拦截器
 */
public interface AopInterceptor {
    void before(Object[] args);
    void after(Object[] args,Object returnValue);
    void around(Object[] args,Object returnValue);
    void afterReturning(Object[] args,Object returnValue);
    void exception(Object[] args,Throwable throwable);
}
