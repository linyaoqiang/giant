package com.giant.aop;

import java.lang.reflect.InvocationHandler;

/**
 * AopProxy代理工厂
 */
public interface AopProxyFactory {
    //单例
    AopProxyFactory INSTANCE=new DefaultAopProxyFactory();
    //创建AopProxy
    AopProxy createAopProxy(Object target, InvocationHandler handler);
}
