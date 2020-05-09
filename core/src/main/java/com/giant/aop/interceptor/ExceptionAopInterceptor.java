package com.giant.aop.interceptor;

import com.giant.commons.opeator.ReflectOperator;

import java.lang.reflect.Method;

public class ExceptionAopInterceptor extends AbstractAopInterceptor {

    public ExceptionAopInterceptor(String pointcut, Method adviceMethod, Object adviceTarget) {
        super(pointcut, adviceMethod, adviceTarget);
    }

    @Override
    public void exception(Object[] args, Throwable throwable) {
        Object[] params = new Object[]{args,throwable};
        ReflectOperator.doMethod(adviceMethod,adviceTarget,params);
    }
}
