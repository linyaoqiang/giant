package com.giant.aop.interceptor;

import java.lang.reflect.Method;

/**
 * 方法拦截器链
 */
public interface AopInterceptorChain {
    void addInterceptor(AopInterceptor interceptor);
    void removeInterceptor(AopInterceptor interceptor);
    void doBefore(Method method, Object[] args);
    void doAfter(Method method,Object[] args,Object returnValue);
    void doAfterReturning(Method method,Object[] args,Object returnValue);
    void doAround(Method method,Object[] args,Object returnValue);
    void doException(Method method,Object[] args,Throwable e);
}
