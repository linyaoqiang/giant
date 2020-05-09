package com.giant.aop.interceptor;

import com.giant.commons.opeator.ReflectOperator;

import java.lang.reflect.Method;

public class AroundAopInterceptor extends AbstractAopInterceptor {
    public AroundAopInterceptor(String pointcut, Method adviceMethod, Object adviceTarget) {
        super(pointcut, adviceMethod, adviceTarget);
    }

    @Override
    public void around(Object[] args, Object returnValue) {
        Object[] params = new Object[]{args,returnValue};
        ReflectOperator.doMethod(adviceMethod,adviceTarget,params);
    }
}
