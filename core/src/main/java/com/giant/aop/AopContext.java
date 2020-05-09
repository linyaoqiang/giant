package com.giant.aop;

import com.giant.annotation.Advice;
import com.giant.annotation.Pointcut;
import com.giant.aop.interceptor.AopInterceptorChain;
import com.giant.aop.interceptor.DefaultAopInterceptorChain;
import com.giant.commons.opeator.FileOperator;
import com.giant.commons.opeator.ReflectOperator;
import com.giant.commons.opeator.StringOperator;
import org.dom4j.Element;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AopContext {
    private AopInterceptorChain chain;
    private boolean useAop = true;
    private Map<String, Object> aopBeans;
    private Map<String, String> pointcuts;
    private AopHelper helper = AopHelper.HELPER;
    private AdviceCreator adviceCreator = AdviceCreator.ADVICE_CREATOR;
    private AopInterceptorChainCreator creator = AopInterceptorChainCreator.CREATOR;

    public AopContext(String config) {
        try {
            adviceCreator.createAdvice(config);
            aopBeans = adviceCreator.getAopBeans();
            pointcuts = adviceCreator.getPointcuts();
            if (aopBeans.size() == 0) {
                useAop = false;
                return;
            }
            chain = creator.createChain(aopBeans, pointcuts);

            if (((DefaultAopInterceptorChain) chain).getInterceptors().size() == 0) {
                useAop = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public AopInterceptorChain getChain() {
        return chain;
    }

    public boolean isUseAop() {
        return useAop;
    }

    public Map<String, Object> getAopBeans() {
        return aopBeans;
    }
}
