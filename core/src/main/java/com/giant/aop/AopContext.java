package com.giant.aop;
import com.giant.aop.interceptor.AopInterceptorChain;
import com.giant.aop.interceptor.DefaultAopInterceptorChain;
import java.util.Map;

/**
 * Aop上下文，用于创建Advice通知实例，以及创建方法拦截链
 */
public class AopContext {
    private AopInterceptorChain chain;
    private boolean useAop = true;
    private Map<String, Object> aopBeans;
    private Map<String, String> pointcuts;
    //这三个是单例的，防止重复创建无用对象
    private AopHelper helper = AopHelper.HELPER;
    private AdviceCreator adviceCreator = AdviceCreator.ADVICE_CREATOR;
    private AopInterceptorChainCreator creator = AopInterceptorChainCreator.CREATOR;

    public AopContext(String config) {
        try {
            //创建通知类实例
            adviceCreator.createAdvice(config);
            aopBeans = adviceCreator.getAopBeans();
            pointcuts = adviceCreator.getPointcuts();


            if (aopBeans.size() == 0) {
                useAop = false;
                return;
            }
            //创建方法拦截链
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
