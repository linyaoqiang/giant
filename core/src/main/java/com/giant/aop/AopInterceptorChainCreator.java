package com.giant.aop;

import com.giant.annotation.*;
import com.giant.aop.interceptor.*;

import java.lang.reflect.Method;
import java.util.Map;

public class AopInterceptorChainCreator {
    public static final AopInterceptorChainCreator CREATOR = new AopInterceptorChainCreator();
    private AopHelper aopHelper = AopHelper.HELPER;

    private AopInterceptorChainCreator() {
    }

    public AopInterceptorChain createChain(Map<String, Object> aopBeans, Map<String, String> pointcuts) {
        AopInterceptorChain chain = new DefaultAopInterceptorChain();
        //遍历没有通知类的实例
        for (String key : aopBeans.keySet()) {
            Object o = aopBeans.get(key);
            String pointcut = pointcuts.get(key);
            //pointcut为解析后的pointcut
            pointcut=aopHelper.getPointName(pointcut);
            Class<?> clazz = o.getClass();
            Method[] methods = clazz.getDeclaredMethods();
            //遍历每一个方法
            for (Method method : methods) {
                addInterceptor(chain,pointcut,method,o);
            }
        }
        return chain;
    }

    public void addInterceptor(AopInterceptorChain chain,String pointcut,Method method,Object target) {
        /**
         * 判断方法注解，创建对应的方法拦截器
         * 允许一个方法有多个拦截器，但是注意before不能和其他一起用
         */
        if(method.getAnnotation(BeforeAdvice.class)!=null){
            chain.addInterceptor(new BeforeAopInterceptor(pointcut,method,target));
        }
        if(method.getAnnotation(AfterAdvice.class)!=null) {
            chain.addInterceptor(new AfterAopInterceptor(pointcut,method,target));
        }

        if(method.getAnnotation(AfterReturningAdvice.class)!=null) {
            chain.addInterceptor(new AfterReturningAopInterceptor(pointcut,method,target));
        }
        if(method.getAnnotation(AroundAdvice.class)!=null) {
            chain.addInterceptor(new AroundAopInterceptor(pointcut,method,target));
        }

        if(method.getAnnotation(ExceptionAdvice.class)!=null){
            chain.addInterceptor(new ExceptionAopInterceptor(pointcut,method,target));
        }
    }
}
