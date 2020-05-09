package com.giant.aop.interceptor;

import com.giant.aop.AopHelper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DefaultAopInterceptorChain implements  AopInterceptorChain{
    private List<AopInterceptor> interceptors = new ArrayList<>();
    private AopHelper helper=AopHelper.HELPER;
    @Override
    public void addInterceptor(AopInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    @Override
    public void removeInterceptor(AopInterceptor interceptor) {
        interceptors.remove(interceptor);
    }

    @Override
    public void doBefore(Method method, Object[] args) {
        for(AopInterceptor interceptor:interceptors){
            if(interceptor instanceof  BeforeAopInterceptor&&helper.isAopPointcut(((BeforeAopInterceptor) interceptor).pointcut,method)){
                interceptor.before(args);
            }
        }
    }

    @Override
    public void doAfter(Method method,Object[] args, Object returnValue) {
        for(AopInterceptor interceptor:interceptors){
            if(interceptor instanceof  AfterAopInterceptor&&helper.isAopPointcut(((AfterAopInterceptor) interceptor).pointcut,method)){
                interceptor.after(args,returnValue);
            }
        }
    }

    @Override
    public void doAfterReturning(Method method,Object[] args, Object returnValue) {
        for(AopInterceptor interceptor:interceptors){
            if(interceptor instanceof  AfterReturningAopInterceptor&&helper.isAopPointcut(((AfterReturningAopInterceptor) interceptor).pointcut,method)){
                interceptor.afterReturning(args,returnValue);
            }
        }
    }

    @Override
    public void doAround(Method method,Object[] args, Object returnValue) {
        for(AopInterceptor interceptor:interceptors){
            if(interceptor instanceof  AroundAopInterceptor&&helper.isAopPointcut(((AroundAopInterceptor) interceptor).pointcut,method)){
                interceptor.around(args,returnValue);
            }
        }
    }

    @Override
    public void doException(Method method,Object[] args, Throwable e) {
        for(AopInterceptor interceptor:interceptors){
            if(interceptor instanceof  ExceptionAopInterceptor&&helper.isAopPointcut(((ExceptionAopInterceptor) interceptor).pointcut,method)){
                interceptor.exception(args,e);
            }
        }
    }

    public List<AopInterceptor> getInterceptors() {
        return interceptors;
    }
}
