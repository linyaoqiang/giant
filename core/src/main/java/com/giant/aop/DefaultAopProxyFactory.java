package com.giant.aop;

import java.lang.reflect.InvocationHandler;

public class DefaultAopProxyFactory implements  AopProxyFactory{

    @Override
    public AopProxy createAopProxy(Object target, InvocationHandler handler) {
        AopProxy aopProxy = new AopProxy(target);
        aopProxy.bind(handler);
        return aopProxy;
    }
}
