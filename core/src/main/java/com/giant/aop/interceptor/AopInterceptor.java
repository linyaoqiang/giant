package com.giant.aop.interceptor;

import java.lang.reflect.Method;

public interface AopInterceptor {
    void before(Object[] args);
    void after(Object[] args,Object returnValue);
    void around(Object[] args,Object returnValue);
    void afterReturning(Object[] args,Object returnValue);
    void exception(Object[] args,Throwable throwable);
}
