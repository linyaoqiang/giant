package com.giant.aop.interceptor;

import com.giant.commons.opeator.ReflectOperator;

import java.lang.reflect.Method;

public class BeforeAopInterceptor extends AbstractAopInterceptor{
    public BeforeAopInterceptor(String pointcut, Method adviceMethod,Object adviceTarget) {
        super(pointcut, adviceMethod,adviceTarget);
    }

    @Override
    public void before(Object[] args) {
        Object[] params = new Object[]{args};
        ReflectOperator.doMethod(adviceMethod,adviceTarget,params);
    }
}
