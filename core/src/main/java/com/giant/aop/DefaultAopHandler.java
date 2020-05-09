package com.giant.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.regex.Pattern;

import com.giant.annotation.Pointcut;
import com.giant.aop.bean.AdviceExecutor;
import com.giant.aop.interceptor.AopInterceptorChain;
import org.apache.log4j.Logger;

public class DefaultAopHandler implements InvocationHandler, AopHandler {
    private AopInterceptorChain chain;
    private Object target;
    private Logger logger = Logger.getLogger(DefaultAopHandler.class);

    public DefaultAopHandler(AopInterceptorChain chain, Object target) {
        super();
        this.target=target;
        this.chain=chain;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Object returnValue = null;
        try {
            chain.doBefore(method, args);
            chain.doAround(method, args, null);
            returnValue = method.invoke(target);
            chain.doAfterReturning(method, args, returnValue);
        } catch (Exception e) {
            e.printStackTrace();
            chain.doException(method, args, e);
        }
        chain.doAfter(method,args,returnValue);
        return returnValue;
    }
}
