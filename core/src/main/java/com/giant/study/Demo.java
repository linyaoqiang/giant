package com.giant.study;

import com.giant.core.BeanFactory;
import com.giant.core.BeanFactoryBuilder;
import com.giant.study.test.HelloWorld;
import com.giant.study.test.HelloWorldImpl;

import java.lang.reflect.Proxy;

public class Demo {
    public static void main(String[] args) throws Exception {
        BeanFactoryBuilder builder = new BeanFactoryBuilder();
        BeanFactory app=builder.build("applicationContext.xml");

    }
}
