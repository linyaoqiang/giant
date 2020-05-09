package com.giant.aop.interceptor;

import java.lang.reflect.Method;

public abstract class AbstractAopInterceptor implements AopInterceptor {
    protected String pointcut;
    protected Method adviceMethod;
    protected Object adviceTarget;

    public AbstractAopInterceptor(String pointcut, Method adviceMethod, Object adviceTarget) {
        this.pointcut = pointcut;
        this.adviceMethod = adviceMethod;
        this.adviceTarget = adviceTarget;
    }

    @Override
    public void before(Object[] args) {

    }

    @Override
    public void after(Object[] args, Object returnValue) {

    }

    @Override
    public void around(Object[] args, Object returnValue) {

    }

    @Override
    public void afterReturning(Object[] args, Object returnValue) {

    }

    @Override
    public void exception(Object[] args, Throwable throwable) {

    }
}
