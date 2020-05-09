package com.giant.study;


import com.giant.commons.opeator.ReflectOperator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class Demo2 {
    public static void main(String[] args) {
        Object o=ReflectOperator.createJdkProxy(new Address("比较"), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("我执行了");
                return null;
            }
        });
    }
}
