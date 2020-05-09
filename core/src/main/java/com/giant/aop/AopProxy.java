package com.giant.aop;

import com.giant.commons.opeator.ReflectOperator;

import java.lang.reflect.InvocationHandler;

/**
 * AopProxy代理，获取代理对象
 */
public class AopProxy {
    private Object proxy;
    private Object target;


    public AopProxy(Object target) {
        this.target = target;
    }

    //核心方法
    public void bind(InvocationHandler handler) {
        //proxy有可能为null
        proxy = ReflectOperator.createJdkProxy(target, handler);
    }
    //获取实例
    public Object getProxy() {
        //如果proxy为null，则直接返回target
        if (proxy == null) {
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
