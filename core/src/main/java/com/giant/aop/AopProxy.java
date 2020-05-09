package com.giant.aop;

import com.giant.aop.interceptor.AopInterceptorChain;
import com.giant.commons.opeator.ReflectOperator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class AopProxy {
    private Object proxy;
    private Object target;


    public AopProxy(Object target) {
        this.target = target;
    }

    public void bind(InvocationHandler handler) {
        proxy=ReflectOperator.createJdkProxy(target,handler);
    }

    public Object getProxy() {
        if(proxy==null){
            return target;
        }
        return proxy;
    }

    public void setProxy(Object proxy) {
        this.proxy = proxy;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

}
