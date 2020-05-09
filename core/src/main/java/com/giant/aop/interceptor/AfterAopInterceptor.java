package com.giant.aop.interceptor;

import com.giant.commons.opeator.ReflectOperator;

import java.lang.reflect.Method;

public class AfterAopInterceptor extends AbstractAopInterceptor {

    public AfterAopInterceptor(String pointcut, Method adviceMethod, Object adviceTarget) {
        super(pointcut, adviceMethod, adviceTarget);
    }

    @Override
    public void after(Object[] args, Object returnValue) {
        Object[] params = new Object[]{args,returnValue};
        ReflectOperator.doMethod(adviceMethod,adviceTarget,params);
    }
}
