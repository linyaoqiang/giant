package com.giant.aop;

import java.lang.reflect.InvocationHandler;

public interface AopProxyFactory {
    AopProxyFactory INSTANCE=new DefaultAopProxyFactory();
    AopProxy createAopProxy(Object target, InvocationHandler handler);
}
